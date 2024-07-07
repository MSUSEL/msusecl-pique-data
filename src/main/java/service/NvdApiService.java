package service;

import businessObjects.HTTPMethod;
import businessObjects.NVDRequest;
import businessObjects.NVDRequestFactory;
import businessObjects.NVDResponse;
import businessObjects.cve.CVEResponse;
import businessObjects.cve.Cve;
import businessObjects.cve.NvdMirrorMetaData;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import common.*;
import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.IBulkDao;
import persistence.IMetaDataDao;

import java.util.List;
import java.util.Properties;

public class NvdApiService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NvdApiService.class);
    private final CveResponseProcessor cveResponseProcessor = new CveResponseProcessor();
    private final DbContextResolver dbContextResolver = new DbContextResolver();
    private final HeaderBuilder hb = new HeaderBuilder();
    private final Properties prop = DataUtilityProperties.getProperties();
    private final String apiKeyHeader = Utils.readFileWithBufferedReader(prop.getProperty("nvd-api-key-path"));
    private final ParameterBuilder parameterBuilder = new ParameterBuilder();

    public Cve handleGetCveFromNvd(String cveId) {
        NVDRequest request = new NVDRequest(
                HTTPMethod.GET,
                Constants.NVD_BASE_URI,
                hb.addHeader(NvdConstants.API_KEY_HEADER_NAME, apiKeyHeader).build(),
                parameterBuilder.addParameter(NvdConstants.CVE_ID_PARAM_NAME, cveId).build());
        NVDResponse response = request.executeRequest();
        CVEResponse cveResponse = response.getCveResponse();

        return cveResponseProcessor.extractSingleCve(cveResponse);
    }

    // TODO This method could probably also handle updating the mirror - Create NvdMirrorManager?
    public void handleGetPaginatedCves(String dbContext, int startIndex, int resultsPerPage) {
        IBulkDao<Cve> bulkDao = dbContextResolver.resolveBulkDao(dbContext);
        IMetaDataDao<NvdMirrorMetaData> metadataDao = dbContextResolver.resolveMetaDataDao(dbContext);
        int cveCount = startIndex + 1;

        for (int i = startIndex; i < cveCount; i += Constants.NVD_MAX_PAGE_SIZE) {
            Header[] headers = hb.addHeader(Constants.API_KEY_HEADER_NAME, apiKeyHeader).build();
            NVDRequest request = NVDRequestFactory.createNVDRequest(HTTPMethod.GET, Constants.NVD_BASE_URI, headers, startIndex, resultsPerPage);
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

        for (int i = startIndex; i < totalResults; i += Constants.NVD_MAX_PAGE_SIZE) {
            NVDRequest request = NVDRequestFactory.createNVDRequest(
                    HTTPMethod.GET,
                    Constants.NVD_BASE_URI,
                    apiKeyHeader,
                    Constants.DEFAULT_START_INDEX,
                    Constants.NVD_MAX_PAGE_SIZE,
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
                Thread.sleep(Constants.DEFAULT_NVD_REQUEST_SLEEP);
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
