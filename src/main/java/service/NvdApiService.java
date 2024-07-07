package service;

import businessObjects.HTTPMethod;
import businessObjects.NVDRequest;
import businessObjects.NVDRequestFactory;
import businessObjects.NVDResponse;
import businessObjects.cve.CVEResponse;
import businessObjects.cve.Cve;
import common.DataUtilityProperties;
import common.HeaderBuilder;
import common.Utils;
import org.apache.http.Header;
import persistence.IBulkDao;
import persistence.IMetaDataDao;
import businessObjects.cve.NvdMirrorMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class NvdApiService {
    private final HeaderBuilder hb = new HeaderBuilder();
    private final Properties prop = DataUtilityProperties.getProperties();
    private final CveResponseProcessor cveResponseProcessor = new CveResponseProcessor();
    private final DbContextResolver dbContextResolver = new DbContextResolver();
    private static final Logger LOGGER = LoggerFactory.getLogger(NvdApiService.class);

    public Cve handleGetCveFromNvd(String cveId) {
        int startIndex = 0;
        int resultsPerPage = 1;
        Header[] headers = hb.addHeader("apiKey", Utils.readFileWithBufferedReader(prop.getProperty("nvd-api-key-path")))
                .addHeader("cveId", cveId)
                .build();


        NVDRequest request = NVDRequestFactory.createNVDRequest(HTTPMethod.GET, Utils.NVD_BASE_URI, apiKeyHeader, startIndex, resultsPerPage);
        NVDResponse response = request.executeRequest();
        CVEResponse cveResponse = response.getCveResponse();

        return cveResponseProcessor.extractSingleCve(cveResponse);
    }

    // TODO This method could probably also handle updating the mirror - Create NvdMirrorManager?
    public void handleGetPaginatedCves(String dbContext, int startIndex, int resultsPerPage) {
        IBulkDao<Cve> bulkDao = dbContextResolver.resolveBulkDao(dbContext);
        IMetaDataDao<NvdMirrorMetaData> metadataDao = dbContextResolver.resolveMetaDataDao(dbContext);
        int cveCount = startIndex + 1;

        for (int i = startIndex; i < cveCount; i += Utils.NVD_MAX_PAGE_SIZE) {
            NVDRequest request = NVDRequestFactory.createNVDRequest(HTTPMethod.GET, Utils.NVD_BASE_URI, apiKeyHeader, startIndex, resultsPerPage);
            NVDResponse response = request.executeRequest();
            CVEResponse cveResponse = response.getCveResponse();

            cveCount = cveResponseProcessor.extractTotalResults(cveResponse);
            List<Cve> cves = cveResponseProcessor.extractAllCves(cveResponse);
            NvdMirrorMetaData nvdMirrorMetaData = cveResponseProcessor.formatNvdMetaData(cveResponse);

            bulkDao.insertMany(cves);   // TODO implement insertMany in postgresBulkDao
            conditionallyInsertMetaData(nvdMirrorMetaData, metadataDao, startIndex, cveCount);
            handleSleep(startIndex, cveCount);
        }
    }

    // TODO could probably remove this method and use handleGetPaginatedCves instead
    public void handleUpdateNvdMirror(String dbContext, String lastModStartDate, String lastModEndDate) {
        IBulkDao<Cve> bulkDao = dbContextResolver.resolveBulkDao(dbContext);
        IMetaDataDao<NvdMirrorMetaData> metadataDao = dbContextResolver.resolveMetaDataDao(dbContext);
        int startIndex = 0;
        int totalResults = startIndex + 1;

        for (int i = startIndex; i < totalResults; i += Utils.NVD_MAX_PAGE_SIZE) {
            NVDRequest request = NVDRequestFactory.createNVDRequest(
                    HTTPMethod.GET,
                    Utils.NVD_BASE_URI,
                    apiKeyHeader,
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
            handleSleep(startIndex, totalResults);
        }
    }

    private void handleSleep(int startIndex, int count) {
        try {
            if (startIndex != count - 1) {
                Thread.sleep(Utils.DEFAULT_NVD_REQUEST_SLEEP);
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
