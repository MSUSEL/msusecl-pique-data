import businessObjects.cveData.NvdMirrorMetaData;
import database.mongo.MongoCveDao;
import org.junit.Test;

import database.IDao;
import database.mongo.NVDMirror;
import database.mongo.MongoBulkCveDao;
import database.mongo.MongoMetaDataDao;
import database.postgreSQL.PostgresCveDao;
import database.postgreSQL.PostgresConnectionManager;
import presentation.PiqueData;

import org.json.JSONException;
import org.json.JSONObject;

import businessObjects.GHSARequest;
import businessObjects.GHSAResponse;
import businessObjects.GraphQlQueries;
import businessObjects.HTTPMethod;
import businessObjects.NVDRequest;
import businessObjects.NVDRequestFactory;
import businessObjects.NVDResponse;
import businessObjects.cveData.Cve;
import businessObjects.cveData.Vulnerability;
import businessObjects.ghsaData.CweNode;
import common.Utils;
import common.DataUtilityProperties;
import service.CveResponseProcessor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class DataUtilityTest {
    private static Properties prop = DataUtilityProperties.getProperties();
    private static final CveResponseProcessor cveResponseProcessor = new CveResponseProcessor();

    @Test
    public void testDataStoreFullBuild() {
        NVDMirror mirror = new NVDMirror();
        mirror.getFullDataSetLocal();
    }

    @Test
    public void testGhsaRequest() throws JSONException {
        String ghsaId = "GHSA-vh2m-22xx-q94f";

        // Define the query string
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("query", GraphQlQueries.GHSA_SECURITY_ADVISORY_QUERY);
        String query = jsonBody.toString();
        String formattedQuery = String.format(query, ghsaId);

        String githubToken = Utils.getAuthToken(prop.getProperty("github-token-path"));
        String authHeader = String.format("Bearer %s", githubToken);
        List<String> headers = Arrays.asList("Content-Type", "application/json", "Authorization", authHeader);

        GHSARequest ghsaRequest = new GHSARequest(HTTPMethod.POST, Utils.GHSA_URI, headers, formattedQuery);
        GHSAResponse ghsaResponse = ghsaRequest.executeRequest();

        assertEquals(200, ghsaResponse.getStatus());
        assertEquals("GHSA-vh2m-22xx-q94f", ghsaResponse.getSecurityAdvisory().getGhsaId());
        assertEquals("Sensitive query parameters logged by default in OpenTelemetry.Instrumentation http and AspNetCore",
                ghsaResponse.getSecurityAdvisory().getSummary());

        ArrayList<CweNode> nodes = ghsaResponse.getSecurityAdvisory().getCwes().getNodes();
        assertEquals("CWE-201", nodes.get(0).getCweId());
    }

    @Test
    public void testSimpleNVDRequest() {
        List<String> apiKeyHeader = Arrays.asList("apiKey", Utils.getAuthToken(prop.getProperty("nvd-api-key-path")));
        NVDRequest nvdRequest = NVDRequestFactory.createNVDRequest(HTTPMethod.GET, Utils.NVD_BASE_URI, apiKeyHeader, 0, 1);
        NVDResponse nvdResponse = nvdRequest.executeRequest();

        assertEquals(200, nvdResponse.getStatus());
        assertEquals("CVE-1999-0095", nvdResponse.getCveResponse().getVulnerabilities().get(0).getCve().getId());
    }

    @Test
    public void testDaoInsertMany() {
        MongoBulkCveDao mongoBulkCveDao = new MongoBulkCveDao();
        List<String> apiKey = Arrays.asList("apiKey", Utils.getAuthToken(prop.getProperty("nvd-api-key-path")));
        NVDResponse response;

        NVDRequest request = NVDRequestFactory.createNVDRequest(HTTPMethod.GET, Utils.NVD_BASE_URI, apiKey, 0, 2000);
        response = request.executeRequest();
        List<Cve> cves = new ArrayList<>();

        for (Vulnerability vulnerability : response.getCveResponse().getVulnerabilities()) {
            cves.add(vulnerability.getCve());
        }

        mongoBulkCveDao.insertMany(cves);
    }

//    @Test
//    public void testMetaDataInsert() {
//        List<String> apiKey = Arrays.asList("apiKey", Utils.getAuthToken(prop.getProperty("nvd-api-key-path")));
//        NVDResponse response;
//
//        NVDRequest request = NVDRequestFactory.createNVDRequest(HTTPMethod.GET, Utils.NVD_BASE_URI, apiKey, 0, 1);
//        response = request.executeRequest();
//
//        MongoMetaDataDao mongoMetaDataDao = new MongoMetaDataDao();
//        mongoMetaDataDao.insert(response.getCveResponse());
//    }

    @Test
    public void testMetaDataReplace() {
        List<String> apiKey = Arrays.asList("apiKey", Utils.getAuthToken(prop.getProperty("nvd-api-key-path")));
        NVDResponse response;

        NVDRequest request = NVDRequestFactory.createNVDRequest(HTTPMethod.GET, Utils.NVD_BASE_URI, apiKey, 0, 1);
        response = request.executeRequest();

        NvdMirrorMetaData metaData= cveResponseProcessor.formatNvdMetaData(response.getCveResponse());
        MongoMetaDataDao mongoMetaDataDao = new MongoMetaDataDao();
        mongoMetaDataDao.updateMetaData(metaData);
    }
    
    @Test
    public void testPostgresConnection() throws IOException, SQLException {
        Connection conn = PostgresConnectionManager.getConnection();
        assertNotNull(conn);
    }
    
    @Test
    public void testPostgresInsert() throws IOException, SQLException {
        Connection conn = PostgresConnectionManager.getConnection();
        // This will definitely break and needs a totally different structure

        // Get a CVE that is currently stored in mongo
        IDao<Cve> mongoDao = new MongoCveDao();
        Cve cve = mongoDao.fetchById("CVE-1999-0095");
        
        // insertMany into postgres
        IDao<Cve> postgresDao = new PostgresCveDao();
        postgresDao.insert(cve);
    }

    @Test
    public void testInteractiveGetCveById() {
        Cve result = PiqueData.getCveById(Utils.DB_CONTEXT_LOCAL,"CVE-1999-0095");
        assertNotNull(result);
    }

    @Test
    public void testInteractiveGetCwes() {
        String[] result = PiqueData.getCwes("local", "CVE-1999-0095");
        assertEquals("NVD-CWE-Other", result[0]);
    }
}

