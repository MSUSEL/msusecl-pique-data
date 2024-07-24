package service;

import businessObjects.interfaces.HTTPMethod;
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

import java.util.List;

public final class NvdApiService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NvdApiService.class);
    private final CveResponseProcessor cveResponseProcessor = new CveResponseProcessor();
    private final DbContextResolver dbContextResolver = new DbContextResolver();

    /**
     * Calls to NVD CVE2.0 API filtering results to single CVE
     * @param cveId the cveId of the CVE in question
     * @return Cve object from NVD response
     */
    public Cve handleGetCveFromNvd(String cveId) {
        NvdRequest request = new NvdRequest(HTTPMethod.GET,
                Constants.NVD_CVE_URI,
                useDefaultHeaders(),
                new ParameterBuilder().addParameter(NvdConstants.CVE_ID, cveId).build());
        CVEResponse response = performApiCall(request);
        return cveResponseProcessor.extractSingleCve(response);
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

            NvdRequest request = new NvdRequest(HTTPMethod.GET,
                    Constants.NVD_CVE_URI,
                    useDefaultHeaders(),
                    buildPaginateParams(i, resultsPerPage, parameterBuilder));
            CVEResponse response = performApiCall(request);
            cveCount = cveResponseProcessor.extractTotalResults(response);
            List<Cve> cves = cveResponseProcessor.extractAllCves(response);
            NvdMirrorMetaData nvdMirrorMetaData = cveResponseProcessor.formatNvdMetaData(response);

            bulkDao.insertMany(cves);
            insertMetaData(nvdMirrorMetaData, metadataDao, startIndex, cveCount, true);
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
            insertMetaData(nvdMirrorMetaData, metadataDao, startIndex, totalResults, false);
            handleSleep(startIndex, totalResults);
        }
    }

    private Header[] useDefaultHeaders() {
        return new HeaderBuilder().addHeader(NvdConstants.API_KEY, System.getenv("NVD_KEY")).build();
    }

    private List<NameValuePair> buildPaginateParams(int index, int resultsPerPage, ParameterBuilder parameterBuilder) {
        return parameterBuilder
                .addParameter(NvdConstants.START_INDEX, Integer.toString(index))
                .addParameter(NvdConstants.RESULTS_PER_PAGE, Integer.toString(resultsPerPage))
                .build();
    }

    private List<NameValuePair> buildUpdateP9arams(int index, String lastModStartDate, String lastModEndDate, ParameterBuilder parameterBuilder) {
        buildPaginateParams(index, Constants.NVD_MAX_PAGE_SIZE, parameterBuilder);
        return parameterBuilder
                .addParameter(NvdConstants.LAST_MOD_START_DATE, lastModStartDate)
                .addParameter(NvdConstants.LAST_MOD_END_DATE, lastModEndDate)
                .build();
    }

    private NvdRequest createRequest() {
        ParameterBuilder parameterBuilder = new ParameterBuilder();


    }

    private CVEResponse performApiCall(NvdRequest request) {
        NvdResponse response = request.executeRequest();
        return response.getCveResponse();
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

    private void insertMetaData(NvdMirrorMetaData metaData, IMetaDataDao<NvdMirrorMetaData> metaDataDao, int startIndex, int count, boolean conditional) throws DataAccessException {
        if(conditional) {
            if (startIndex == count - 1) {
                metaDataDao.updateMetaData(metaData);
            }
        } else {
            metaDataDao.updateMetaData(metaData);
        }
    }

}
