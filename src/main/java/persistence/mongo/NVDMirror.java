package persistence.mongo;

import businessObjects.HTTPMethod;
import businessObjects.NVDRequest;
import businessObjects.NVDRequestFactory;
import businessObjects.NVDResponse;
import businessObjects.cve.Cve;
import businessObjects.cve.NvdMirrorMetaData;
import businessObjects.cve.Vulnerability;
import common.DataUtilityProperties;
import common.Utils;
import persistence.IBulkDao;

import persistence.IMetaDataDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.CveResponseProcessor;

import java.util.*;

public class NVDMirror {
    private static final Logger LOGGER = LoggerFactory.getLogger(NVDMirror.class);
    private final Properties prop = DataUtilityProperties.getProperties();
    private final List<String> apiKeyHeader = Arrays.asList("apiKey", Utils.getAuthToken(prop.getProperty("nvd-api-key-path")));
    private final IBulkDao<Cve> bulkCveDao = new MongoBulkCveDao();
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
}

