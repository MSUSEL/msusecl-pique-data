import businessObjects.cveData.Cve;
import common.DataUtilityProperties;
import common.Utils;
import exceptions.DataAccessException;
import handlers.CveMarshaller;
import handlers.IJsonMarshaller;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import presentation.PiqueNvdMirror;

import java.util.Properties;

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

        try {
            PiqueNvdMirror.insertSingleCve(postgresContext, cve);
        } catch (DataAccessException e) {
            log.error("Query failed with error: ", e);
            throw new RuntimeException(e);
        }
    }
}
