package service;

import api.HTTPMethod;
import api.NVDRequest;
import api.NVDRequestFactory;
import api.NVDResponse;
import api.cveData.CVEResponse;
import api.cveData.Cve;
import common.DataProperties;
import common.Utils;
import database.IBulkDao;
import database.IMetaDataDao;
import api.cveData.NvdMirrorMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class NvdApiService {
    private final Properties prop = DataProperties.getProperties();
    private final List<String> apiKey = Arrays.asList("apiKey", Utils.getAuthToken(prop.getProperty("nvd-api-key-path")));
    private final CveResponseProcessor cveResponseProcessor = new CveResponseProcessor();
    private final DbContextResolver dbContextResolver = new DbContextResolver();
    private static final Logger LOGGER = LoggerFactory.getLogger(NvdApiService.class);

    public Cve handleGetCveFromNvd(String cveId) {
        int startIndex = 0;
        int resultsPerPage = 1;

        NVDRequest request = NVDRequestFactory.createNVDRequest(HTTPMethod.GET, Utils.NVD_BASE_URI, apiKey, startIndex, resultsPerPage);
        NVDResponse response = request.executeRequest();
        CVEResponse cveResponse = response.getCveResponse();

        return cveResponseProcessor.extractSingleCve(cveResponse);
    }

    // TODO This method could probably also handle updating the mirror - Create NvdMirrorManager?
    public void handleGetPaginatedCves(String dbContext, int startIndex, int resultsPerPage) {
        IBulkDao<List<Cve>> bulkDao = dbContextResolver.getBulkDao(dbContext);
        IMetaDataDao<NvdMirrorMetaData> metadataDao = dbContextResolver.getMetaDataDao(dbContext);
        int cveCount = startIndex + 1;

        for (int i = startIndex; i < cveCount; i += Utils.NVD_MAX_PAGE_SIZE) {
            NVDRequest request = NVDRequestFactory.createNVDRequest(HTTPMethod.GET, Utils.NVD_BASE_URI, apiKey, startIndex, resultsPerPage);
            NVDResponse response = request.executeRequest();
            CVEResponse cveResponse = response.getCveResponse();

            cveCount = cveResponseProcessor.extractTotalResults(cveResponse);
            List<Cve> cves = cveResponseProcessor.extractAllCves(cveResponse);
            NvdMirrorMetaData nvdMirrorMetaData = cveResponseProcessor.formatNvdMetaData(cveResponse);

            bulkDao.insertMany(cves);   // TODO implement insertMany in postgresBulkDao
            conditionallyInsertMetaData(nvdMirrorMetaData, metadataDao, startIndex, cveCount);
            handleSleep(Utils.DEFAULT_NVD_REQUEST_SLEEP, startIndex, cveCount);
        }
    }

    // TODO could probably remove this method and use handleGetPaginatedCves instead
    public void handleUpdateNvdMirror(String dbContext, String lastModStartDate, String lastModEndDate) {
        IBulkDao<List<Cve>> bulkDao = dbContextResolver.getBulkDao(dbContext);
        IMetaDataDao<NvdMirrorMetaData> metadataDao = dbContextResolver.getMetaDataDao(dbContext);
        int startIndex = 0;
        int totalResults = startIndex + 1;

        for (int i = startIndex; i < totalResults; i += Utils.NVD_MAX_PAGE_SIZE) {
            NVDRequest request = NVDRequestFactory.createNVDRequest(
                    HTTPMethod.GET,
                    Utils.NVD_BASE_URI,
                    apiKey,
                    Utils.DEFAULT_START_INDEX,
                    Utils.NVD_MAX_PAGE_SIZE,
                    lastModStartDate,
                    lastModEndDate);
            NVDResponse response = request.executeRequest();
            CVEResponse cveResponse = response.getCveResponse();

            totalResults = cveResponseProcessor.extractTotalResults(cveResponse);
            List<Cve> cves = cveResponseProcessor.extractAllCves(cveResponse);
            NvdMirrorMetaData nvdMirrorMetaData = cveResponseProcessor.formatNvdMetaData(cveResponse);

            bulkDao.insertMany(cves);
            conditionallyInsertMetaData(nvdMirrorMetaData, metadataDao, startIndex, totalResults);
            handleSleep(Utils.DEFAULT_NVD_REQUEST_SLEEP, startIndex, totalResults);
        }
    }

    private void handleSleep(int length, int startIndex, int count) {
        try {
            if (startIndex != count - 1) {
                Thread.sleep(length);
            }
        } catch (InterruptedException e) {
            LOGGER.error("Thread interrupted", e);   // not sure if this is reachable in single-threaded code
            throw new RuntimeException(e);
        }
    }

    private void conditionallyInsertMetaData(NvdMirrorMetaData metaData, IMetaDataDao<NvdMirrorMetaData> metaDataDao, int startIndex, int count) {
        if (startIndex == count - 1) {
            metaDataDao.updateMetaData(metaData);
        }
    }

}
