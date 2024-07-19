package service;

import businessObjects.HTTPMethod;
import businessObjects.NvdRequest;
import businessObjects.NvdResponse;
import businessObjects.cve.CVEResponse;
import businessObjects.cve.Cve;
import businessObjects.cve.NvdMirrorMetaData;
import common.*;
import exceptions.DataAccessException;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.IBulkDao;
import persistence.IMetaDataDao;
import presentation.CveResponseProcessor;

import java.util.List;
import java.util.Properties;

public final class NvdApiService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NvdApiService.class);
    private final CveResponseProcessor cveResponseProcessor = new CveResponseProcessor();
    private final DbContextResolver dbContextResolver = new DbContextResolver();
    private final Properties prop = DataUtilityProperties.getProperties();
    private final String apiKey = Utils.getAuthToken(prop.getProperty(Constants.NVD_API_KEY_PATH));

    /**
     * Calls to NVD CVE2.0 API filtering results to single CVE
     * @param cveId the cveId of the CVE in question
     * @return Cve object from NVD response
     */
    public Cve handleGetCveFromNvd(String cveId) {
        NvdRequest request = new NvdRequest(HTTPMethod.GET, Constants.NVD_CVE_URI, useDefaultHeaders(), new ParameterBuilder().addParameter(NvdConstants.CVE_ID, cveId).build());
        NvdResponse response = request.executeRequest();
        CVEResponse cveResponse = response.getCveResponse();

        return cveResponseProcessor.extractSingleCve(cveResponse);
    }

    /**
     * Gets CVEs in bulk from the NVD and stores them in the given database context.
     * This is primarily used to build the SECL's NVD mirror
     *
     * @param dbContext defines database context (currently SECL's postgres NVD Mirror)
     * @param startIndex Where in the index of CVEs should the NVD begin returning paginated results
     * @param resultsPerPage Total number of results per call - typically the max number of 2000
     */
    public void handleGetPaginatedCves(String dbContext, int startIndex, int resultsPerPage) throws DataAccessException {
        IBulkDao<Cve> bulkDao = dbContextResolver.resolveBulkDao(dbContext);
        IMetaDataDao<NvdMirrorMetaData> metadataDao = dbContextResolver.resolveMetaDataDao(dbContext);
        int cveCount = startIndex + 1;

        for (int i = startIndex; i < cveCount; i += Constants.NVD_MAX_PAGE_SIZE) {
            ParameterBuilder parameterBuilder = new ParameterBuilder();

            NvdRequest request = new NvdRequest(HTTPMethod.GET, Constants.NVD_CVE_URI, useDefaultHeaders(), buildPaginateParams(i, resultsPerPage, parameterBuilder));
            NvdResponse response = request.executeRequest();
            CVEResponse cveResponse = response.getCveResponse();

            cveCount = cveResponseProcessor.extractTotalResults(cveResponse);
            List<Cve> cves = cveResponseProcessor.extractAllCves(cveResponse);
            NvdMirrorMetaData nvdMirrorMetaData = cveResponseProcessor.formatNvdMetaData(cveResponse);

            bulkDao.insertMany(cves);
            conditionallyInsertMetaData(nvdMirrorMetaData, metadataDao, startIndex, cveCount);
            handleSleep(startIndex, cveCount);
        }
    }


    /**
     * Handles updating the SECL NVD Mirror
     * @param dbContext defines database context (currently SECL's postgres
     *                  NVD Mirror or local containerized mongodb instance)
     * @param lastModStartDate Timestamp of previous call to NVD CVE API to update SECL mirror
     * @param lastModEndDate Typically it is the current time - but provides the upper bound to the time window
     *                       from which to pull updates
     */
    public void handleUpdateNvdMirror(String dbContext, String lastModStartDate, String lastModEndDate) throws DataAccessException {
        IBulkDao<Cve> bulkDao = dbContextResolver.resolveBulkDao(dbContext);
        IMetaDataDao<NvdMirrorMetaData> metadataDao = dbContextResolver.resolveMetaDataDao(dbContext);
        int startIndex = 0;
        int totalResults = startIndex + 1;

        for (int i = startIndex; i < totalResults; i += Constants.NVD_MAX_PAGE_SIZE) {
            ParameterBuilder parameterBuilder = new ParameterBuilder();

            NvdRequest request = new NvdRequest(HTTPMethod.GET, Constants.NVD_CVE_URI, useDefaultHeaders(), buildUpdateParams(i, lastModStartDate, lastModEndDate, parameterBuilder));
            NvdResponse response = request.executeRequest();
            CVEResponse cveResponse = response.getCveResponse();

            totalResults = cveResponseProcessor.extractTotalResults(cveResponse);
            List<Cve> cves = cveResponseProcessor.extractAllCves(cveResponse);
            NvdMirrorMetaData nvdMirrorMetaData = cveResponseProcessor.formatNvdMetaData(cveResponse);

            bulkDao.insertMany(cves);
            conditionallyInsertMetaData(nvdMirrorMetaData, metadataDao, startIndex, totalResults);
            handleSleep(startIndex, totalResults);
        }
    }

    private Header[] useDefaultHeaders() {
        return new HeaderBuilder().addHeader(NvdConstants.API_KEY, apiKey).build();
    }

    private List<NameValuePair> buildPaginateParams(int index, int resultsPerPage, ParameterBuilder parameterBuilder) {
        return parameterBuilder
                .addParameter(NvdConstants.START_INDEX, Integer.toString(index))
                .addParameter(NvdConstants.RESULTS_PER_PAGE, Integer.toString(resultsPerPage))
                .build();
    }

    private List<NameValuePair> buildUpdateParams(int index, String lastModStartDate, String lastModEndDate, ParameterBuilder parameterBuilder) {
        buildPaginateParams(index, Constants.NVD_MAX_PAGE_SIZE, parameterBuilder);
        return parameterBuilder
                .addParameter(NvdConstants.LAST_MOD_START_DATE, lastModStartDate)
                .addParameter(NvdConstants.LAST_MOD_END_DATE, lastModEndDate)
                .build();
    }

    private void handleSleep(int startIndex, int count) {
        try {
            if (startIndex != count - 1) {
                Thread.sleep(Constants.DEFAULT_NVD_REQUEST_SLEEP);
            }
        } catch (InterruptedException e) {
            LOGGER.error("Thread interrupted", e);   // not sure if this is reachable in single-threaded code
            throw new RuntimeException(e);
        }
    }

    private void conditionallyInsertMetaData(NvdMirrorMetaData metaData, IMetaDataDao<NvdMirrorMetaData> metaDataDao, int startIndex, int count) throws DataAccessException {
        if (startIndex == count - 1) {
            metaDataDao.updateMetaData(metaData);
        }
    }

}
