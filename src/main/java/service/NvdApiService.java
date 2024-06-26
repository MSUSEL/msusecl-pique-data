package service;

import api.HTTPMethod;
import api.NVDRequest;
import api.NVDRequestFactory;
import api.NVDResponse;
import api.cveData.CVEResponse;
import api.cveData.Cve;
import api.cveData.Vulnerability;
import common.DataProperties;
import common.Utils;
import database.IBulkDao;
import database.IMetaDataDao;
import database.mongo.MongoBulkCveDao;
import database.mongo.MongoMetaDataDao;
import api.cveData.NvdMirrorMetaData;
import database.postgreSQL.PostgresBulkCveDao;
import database.postgreSQL.PostgresMetaDataDao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class NvdApiService {
    private final Properties prop = DataProperties.getProperties();
    private final List<String> apiKey = Arrays.asList("apiKey", Utils.getAuthToken(prop.getProperty("nvd-api-key-path")));
    private final CveResponseProcessor cveResponseProcessor = new CveResponseProcessor();

    public Cve handleGetCveFromNvd(String cveId) {
        int startIndex = 0;
        int resultsPerPage = 1;

        NVDRequest request = NVDRequestFactory.createNVDRequest(HTTPMethod.GET, Utils.NVD_BASE_URI, apiKey, startIndex, resultsPerPage);
        NVDResponse response = request.executeRequest();
        CVEResponse cveResponse = response.getCveResponse();

        return cveResponseProcessor.extractSingleCve(cveResponse);
    }

    public void handleGetPaginatedCves(String dbContext, int startIndex, int resultsPerPage) {
        IBulkDao<List<Cve>> bulkDao = getBulkDao(dbContext);
        IMetaDataDao<NvdMirrorMetaData> metadataDao = getMetaDataDao(dbContext);
        int cveCount = startIndex + 1;

        for (int i = startIndex; i < cveCount; i += Utils.NVD_MAX_PAGE_SIZE) {
            NVDRequest request = NVDRequestFactory.createNVDRequest(HTTPMethod.GET, Utils.NVD_BASE_URI, apiKey, startIndex, resultsPerPage);
            NVDResponse response = request.executeRequest();
            CVEResponse cveResponse = response.getCveResponse();

            cveCount = cveResponseProcessor.extractTotalResults(cveResponse);
            ArrayList<Vulnerability> vulnerabilities = cveResponseProcessor.extractVulnerabilities(cveResponse);
            List<Cve> cves = new ArrayList<>();

            for (Vulnerability vulnerability : vulnerabilities) {
                cves.add(vulnerability.getCve());
            }
            // TODO implement insertMany in postgresBulkDao
            bulkDao.insertMany(cves);

            NvdMirrorMetaData nvdMirrorMetaData = cveResponseProcessor.formatNvdMetaData(cveResponse);
            metadataDao.updateMetaData(nvdMirrorMetaData);
        }
    }

    // TODO Future Work: This is too coupled so consider DI for dbContext/DAO.
    // TODO Possibly use a project-level configuration via the pom file for dbContext?
    private IBulkDao<List<Cve>> getBulkDao(String dbContext) {
        return dbContext.equals(Utils.DB_CONTEXT_LOCAL) ? new MongoBulkCveDao() : new PostgresBulkCveDao();
    }

    private IMetaDataDao<NvdMirrorMetaData> getMetaDataDao(String dbContext) {
        return dbContext.equals(Utils.DB_CONTEXT_LOCAL) ? new MongoMetaDataDao() : new PostgresMetaDataDao();
    }
}