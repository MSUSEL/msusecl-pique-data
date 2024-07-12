import businessObjects.cve.Cve;
import businessObjects.cve.NvdMirrorMetaData;
import common.Constants;
import exceptions.DataAccessException;
import org.junit.Test;
import presentation.NvdMirror;
import presentation.PiqueData;

/**
 * IMPORTANT!
 * These currently mutate the production database
 * Only run these if you know exactly what you're doing!
 */

// TODO mock database to test methods
public class NvdMirrorIntegrationTests {

    @Test
    public void testBuildLocalNvdMirror() throws DataAccessException {
        NvdMirror.buildNvdMirror(Constants.DB_CONTEXT_LOCAL);
    }

    @Test
    public void testBuildPersistentNvdMirror() throws DataAccessException {
        NvdMirror.buildNvdMirror(Constants.DB_CONTEXT_PERSISTENT);
    }

    @Test
    public void testUpdateLocalNvdMirror() throws DataAccessException {
        NvdMirror.updateNvdMirror(Constants.DB_CONTEXT_LOCAL);
    }

    @Test
    public void testUpdatePersistent() throws DataAccessException {
        NvdMirror.updateNvdMirror(Constants.DB_CONTEXT_PERSISTENT);
    }

    @Test
    public void testGetLocalMetaData() throws DataAccessException {
        NvdMirrorMetaData metaData = NvdMirror.getMetaData(Constants.DB_CONTEXT_LOCAL);
    }

    @Test
    public void testGetPersistentMetaData() throws DataAccessException {
        NvdMirrorMetaData metaData = NvdMirror.getMetaData(Constants.DB_CONTEXT_PERSISTENT);
    }

    @Test
    public void testLocalInsertSingleCve() throws DataAccessException {
        // TODO replace this with mocked Cve object
        Cve cve = PiqueData.getCveById(Constants.DB_CONTEXT_LOCAL, "CVE-1999-0095");
        NvdMirror.insertSingleCve(Constants.DB_CONTEXT_LOCAL, cve);
    }

    @Test
    public void testPersistentInsertSingleCve() throws DataAccessException {
        // TODO replace this with mocked Cve object
        Cve cve = PiqueData.getCveById(Constants.DB_CONTEXT_LOCAL, "CVE-1999-0095");
        NvdMirror.insertSingleCve(Constants.DB_CONTEXT_PERSISTENT, cve);
    }
}
