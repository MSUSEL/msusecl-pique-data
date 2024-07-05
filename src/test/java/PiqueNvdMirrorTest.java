import businessObjects.NVDRequest;
import businessObjects.NVDRequestFactory;
import businessObjects.cve.Cve;
import common.DataUtilityProperties;
import common.Utils;
import exceptions.ApiCallException;
import exceptions.DataAccessException;
import handlers.CveMarshaller;
import handlers.IJsonMarshaller;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import persistence.postgreSQL.PgTableOperationsDao;
import presentation.PiqueData;
import presentation.PiqueNvdMirror;

import java.util.Properties;

import static org.junit.Assert.assertNotNull;

/**
 * These tests currently mutate the production database
 * Mocking the database and Data Access Objects will be
 * necessary to unit test this class properly
 */
@Slf4j
public class PiqueNvdMirrorTest {
    private final Properties prop = DataUtilityProperties.getProperties();
    // private final String dbContext = prop.getProperty("db-context");
    private final String mongoContext = Utils.DB_CONTEXT_LOCAL;
    private final String postgresContext = Utils.DB_CONTEXT_PERSISTENT;

    @Test
    public void testBuildFullMirrorWithNewCode() {
        PiqueNvdMirror.buildNvdMirror(mongoContext);
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
            PiqueNvdMirror.insertSingleCve(dbContext, cve);
        } catch (DataAccessException e) {
            log.error("Query failed with error: ", e);
            throw new RuntimeException(e);
        }
    }
}
