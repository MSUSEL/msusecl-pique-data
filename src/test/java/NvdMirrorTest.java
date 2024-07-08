import businessObjects.HTTPMethod;
import businessObjects.NvdRequest;
import businessObjects.NvdResponse;
import businessObjects.cve.Cve;
import common.*;
import exceptions.ApiCallException;
import exceptions.DataAccessException;
import handlers.CveMarshaller;
import handlers.IJsonMarshaller;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import persistence.postgreSQL.PgTableOperationsDao;
import presentation.PiqueData;
import presentation.NvdMirror;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * These tests currently mutate the production database
 * Mocking the database and Data Access Objects will be
 * necessary to unit test this class properly
 */
@Slf4j
public class NvdMirrorTest {
    private final Properties prop = DataUtilityProperties.getProperties();
    // private final String dbContext = prop.getProperty("db-context");
    private final String mongoContext = Constants.DB_CONTEXT_LOCAL;
    private final String postgresContext = Constants.DB_CONTEXT_PERSISTENT;
    private final HeaderBuilder headerBuilder = new HeaderBuilder();
    private final String apiKey = Utils.getAuthToken(prop.getProperty("nvd-api-key-path"));
    private final ParameterBuilder parameterBuilder = new ParameterBuilder();

    @Test
    public void testBuildFullMirrorWithNewCode() {
        NvdMirror.buildNvdMirror(mongoContext);
    }

    @Test
    public void testNvdMirrorService() {
        headerBuilder.addHeader(NvdConstants.API_KEY, apiKey).build();
        NvdRequest nvdRequest = new NvdRequest(
                HTTPMethod.GET,
                Constants.NVD_CVE_URI,
                headerBuilder.addHeader(NvdConstants.API_KEY, apiKey).build(),
                parameterBuilder.addParameter(NvdConstants.CVE_ID, "CVE-1999-0095").build());
        NvdResponse nvdResponse = nvdRequest.executeRequest();

        assertEquals(200, nvdResponse.getStatus());
        assertEquals("CVE-1999-0095", nvdResponse.getCveResponse().getVulnerabilities().get(0).getCve().getId());
    }

    @Test
    public void testInsertSingleCve() {
        IJsonMarshaller<Cve> jsonMarshaller = new CveMarshaller();
        String json = Utils.readFileWithBufferedReader("src/test/resources/test.json");
        Cve cve = jsonMarshaller.unmarshalJson(json);

        runInsertCve(postgresContext, cve);
        runInsertCve(mongoContext, cve);
    }

    @Test
    public void testBuildNvdMirrorTable() {
        PgTableOperationsDao dao = new PgTableOperationsDao();
        dao.buildCveTable();
    }

    @Test
    public void testGetCveFromNvd() throws ApiCallException {
        Cve cve;
        cve = PiqueData.getCveFromNvd("CVE-1999-0095");
        assertNotNull(cve);
    }

    private void runInsertCve(String dbContext, Cve cve) {
        try {
            NvdMirror.insertSingleCve(dbContext, cve);
        } catch (DataAccessException e) {
            log.error("Query failed with error: ", e);
            throw new RuntimeException(e);
        }
    }
}
