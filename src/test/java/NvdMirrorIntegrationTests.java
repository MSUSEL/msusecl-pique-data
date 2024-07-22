import businessObjects.cve.Cve;
import businessObjects.cve.NvdMirrorMetaData;
import common.Constants;
import exceptions.ApiCallException;
import exceptions.DataAccessException;
import org.junit.Test;
import presentation.NvdMirror;
import presentation.PiqueData;

import java.sql.SQLException;

/**
 * IMPORTANT!
 * Some of these currently mutate the production database
 * Only run these if you know exactly what you're doing!
 */

// TODO mock database to test methods
public class NvdMirrorIntegrationTests {

    @Test
    public void testBuildLocalNvdMirror() throws DataAccessException, SQLException {
        NvdMirror.buildNvdMirror(Constants.DB_CONTEXT_LOCAL);
    }

    @Test
    public void testBuildPersistentNvdMirror() throws DataAccessException, SQLException {
        NvdMirror.buildNvdMirror(Constants.DB_CONTEXT_PERSISTENT);
    }

    @Test
    public void testUpdateLocalNvdMirror() throws DataAccessException, ApiCallException {
        NvdMirror.updateNvdMirror(Constants.DB_CONTEXT_LOCAL);
    }

    @Test
    public void testUpdatePersistent() throws DataAccessException, ApiCallException {
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
        Cve cve = PiqueData.getCveById(Constants.DB_CONTEXT_LOCAL, TestConstants.CVE_A);
        NvdMirror.insertSingleCve(Constants.DB_CONTEXT_LOCAL, cve);
    }

    @Test
    public void testPersistentInsertSingleCve() throws DataAccessException {
        // TODO replace this with mocked Cve object
        Cve cve = PiqueData.getCveById(Constants.DB_CONTEXT_LOCAL, TestConstants.CVE_A);
        NvdMirror.insertSingleCve(Constants.DB_CONTEXT_PERSISTENT, cve);
    }

    @Test
    public void testGetCveFromMirror() throws DataAccessException {
        Cve cve = PiqueData.getCveById(Constants.DB_CONTEXT_PERSISTENT, TestConstants.CVE_A);
        System.out.println(cve.getId());
    }
}
