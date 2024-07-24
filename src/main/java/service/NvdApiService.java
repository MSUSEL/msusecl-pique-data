package service;

import businessObjects.NvdRequestBuilder;
import businessObjects.NvdRequest;
import businessObjects.NvdResponse;
import businessObjects.cve.CVEResponse;
import businessObjects.cve.Cve;
import businessObjects.cve.NvdMirrorMetaData;
import common.*;
import exceptions.DataAccessException;
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
        NvdRequest request = new NvdRequestBuilder().withCveId(cveId).build();
        CVEResponse response = performApiCall(request);

        return cveResponseProcessor.extractSingleCve(response);
    }

    /**
     * Gets CVEs in bulk from the NVD and stores them in the given database context.
     * This is primarily used to build the SECL's NVD mirror
     *
     * @param dbContext defines database context (currently SECL's postgres NVD Mirror)
     */
    public void handleBuildMirror(String dbContext) throws DataAccessException {
        int cveCount = 1;

        for (int i = 0; i < cveCount; i += Constants.NVD_MAX_PAGE_SIZE) {
            CVEResponse response = performApiCall(
                    new NvdRequestBuilder().withFullMirrorDefaults(Integer.toString(i)).build());
            cveCount = setCveCount(i, response);
            persistData(dbContext, response, i, cveCount);
            handleSleep(i, cveCount);
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
            NvdRequest request = new NvdRequestBuilder()
                    .withApiKey(System.getenv("NVD_KEY"))
                    .withStartIndex(Integer.toString(i))
                    .withResultsPerPage(Integer.toString(Constants.NVD_MAX_PAGE_SIZE))
                    .withLastModStartEndDates(lastModStartDate, lastModEndDate)
                    .build();
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

    private CVEResponse performApiCall(NvdRequest request) {
        NvdResponse response = request.executeRequest();
        return response.getCveResponse();
    }

    private int setCveCount(int loopIndex, CVEResponse response) {
        return loopIndex == 0
                ? cveResponseProcessor.extractTotalResults(response)
                : 0;
    }

    private void persistData(String dbContext, CVEResponse response, int loopIndex, int cveCount) throws DataAccessException {
        persistCveDetails(dbContext, response);
        if(loopIndex == cveCount - 1) {
            persistMetadata(dbContext, response);
        }
    }

    private void persistCveDetails(String dbContext, CVEResponse response) throws DataAccessException {
        IBulkDao<Cve> dao = dbContextResolver.resolveBulkDao(dbContext);
        dao.insertMany(cveResponseProcessor.extractAllCves(response));
    }

    private void persistMetadata(String dbContext, CVEResponse response) throws DataAccessException {
        IMetaDataDao<NvdMirrorMetaData> dao = dbContextResolver.resolveMetaDataDao(dbContext);
        dao.updateMetaData(cveResponseProcessor.formatNvdMetaData(response));
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
}
