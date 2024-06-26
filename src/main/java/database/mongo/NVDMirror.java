package database.mongo;

import api.HTTPMethod;
import api.NVDRequest;
import api.NVDRequestFactory;
import api.NVDResponse;
import api.cveData.CVEResponse;
import api.cveData.Cve;
import api.cveData.NvdMirrorMetaData;
import api.cveData.Vulnerability;
import common.DataProperties;
import common.Utils;
import database.IBulkDao;
import database.IDao;

import database.IMetaDataDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.CveResponseProcessor;

import java.util.*;

public class NVDMirror {
    private static final Logger LOGGER = LoggerFactory.getLogger(NVDMirror.class);
    private final Properties prop = DataProperties.getProperties();
    private final List<String> apiKeyHeader = Arrays.asList("apiKey", Utils.getAuthToken(prop.getProperty("nvd-api-key-path")));
    private final IBulkDao<List<Cve>> bulkCveDao = new MongoBulkCveDao();
    private final IMetaDataDao<NvdMirrorMetaData> metaDataDao = new MongoMetaDataDao();
    private final CveResponseProcessor cveResponseProcessor = new CveResponseProcessor();

    public void getFullDataSetLocal() {
        int cveCount = 1;
        NvdMirrorMetaData metaData = new NvdMirrorMetaData();

        for (int startIndex = 0; startIndex < cveCount; startIndex += Utils.NVD_MAX_PAGE_SIZE) {
            NVDRequest request = NVDRequestFactory.createNVDRequest(HTTPMethod.GET, Utils.NVD_BASE_URI, apiKeyHeader, startIndex, Utils.NVD_MAX_PAGE_SIZE);
            NVDResponse response = request.executeRequest();
            cveCount = response.getCveResponse().getTotalResults(); // reset cveCount to correctly handle pagination
            ArrayList<Vulnerability> vulnerabilities = response.getCveResponse().getVulnerabilities();
            List<Cve> cves = new ArrayList<>();

            for(Vulnerability vulnerability : vulnerabilities) {
                cves.add(vulnerability.getCve());
            }

            bulkCveDao.insertMany(cves);

            if (startIndex == cveCount - 1) {
                metaData = cveResponseProcessor.formatNvdMetaData(response.getCveResponse());
            }

            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                LOGGER.error("Thread interrupted", e);   // not sure if this is reachable in single-threaded code
                throw new RuntimeException(e);
            }
        }
        metaDataDao.updateMetaData(metaData);
    }

    // TODO Test this method!!! This hasn't been run yet
    // ISO-8601 date/time format: [YYYY][“-”][MM][“-”][DD][“T”][HH][“:”][MM][“:”][SS][Z]
    public void updateLocalNvdMirror(String lastModStartDate, String lastModEndDate) {
        NVDResponse response;
        int cveCount = 1;

        for (int startIndex = 0; startIndex < cveCount; startIndex += Utils.NVD_MAX_PAGE_SIZE) {
            NVDRequest request = NVDRequestFactory.createNVDRequest(
                    HTTPMethod.GET, Utils.NVD_BASE_URI, apiKeyHeader, startIndex, Utils.NVD_MAX_PAGE_SIZE, lastModStartDate, lastModEndDate
            );
            response = request.executeRequest();
            cveCount = response.getCveResponse().getTotalResults();
            ArrayList<Vulnerability> vulnerabilities = response.getCveResponse().getVulnerabilities();

            for (Vulnerability vulnerability : vulnerabilities) {
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    public void getFullDataSetPersistent() {
        int cveCount = 1;
        for (int startIndex = 0; startIndex < cveCount; startIndex += Utils.NVD_MAX_PAGE_SIZE) {
            // TODO Store full data in postgres
        }
    }
}

