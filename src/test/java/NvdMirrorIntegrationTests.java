import businessObjects.cve.Cve;
import businessObjects.cve.NvdMirrorMetaData;
import common.Constants;
import exceptions.ApiCallException;
import exceptions.DataAccessException;
import org.junit.Test;
import persistence.IDataSource;
import persistence.IMetaDataDao;
import persistence.postgreSQL.PostgresConnectionManager;
import persistence.postgreSQL.PostgresMetaDataDao;
import presentation.NvdMirror;
import presentation.PiqueData;

import java.sql.Connection;

/**
 * IMPORTANT!
 * Some of these currently mutate the production database
 * Only run these if you know exactly what you're doing!
 */

// TODO mock database to test methods
public class NvdMirrorIntegrationTests {

    @Test
    public void testBuildLocalNvdMirror() throws DataAccessException, ApiCallException {
        NvdMirror.buildNvdMirror(Constants.DB_CONTEXT_LOCAL);
    }

    @Test
    public void testBuildPersistentNvdMirror() throws DataAccessException, ApiCallException {
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
        System.out.println(metaData.getId());
        System.out.println(metaData.getTotalResults());
        System.out.println(metaData.getFormat());
        System.out.println(metaData.getVersion());
        System.out.println(metaData.getTimestamp());
    }

    @Test
    public void testLocalInsertSingleCve() throws DataAccessException {
        // TODO replace this with mocked Cve object
        Cve cve = PiqueData.getCve(Constants.DB_CONTEXT_LOCAL, TestConstants.CVE_A);
        NvdMirror.insertSingleCve(Constants.DB_CONTEXT_LOCAL, cve);
    }

    @Test
    public void testPersistentInsertSingleCve() throws DataAccessException {
        // TODO replace this with mocked Cve object
        Cve cve = PiqueData.getCve(Constants.DB_CONTEXT_LOCAL, TestConstants.CVE_A);
        NvdMirror.insertSingleCve(Constants.DB_CONTEXT_PERSISTENT, cve);
    }

    @Test
    public void testGetCveFromMirror() throws DataAccessException {
        Cve cve = PiqueData.getCve(Constants.DB_CONTEXT_PERSISTENT, TestConstants.CVE_A);
        System.out.println(cve.getId());
    }

    @Test
    public void testInsertMetaData() throws DataAccessException {
        IDataSource<Connection> dataSource = new PostgresConnectionManager();
        IMetaDataDao<NvdMirrorMetaData> dao = new PostgresMetaDataDao(dataSource);
        NvdMirrorMetaData metaData = new NvdMirrorMetaData();
        metaData.setTimestamp("2024-07-07T23:26:08.260");
        metaData.setId("1");
        metaData.setVersion("2.0");
        metaData.setTotalResults("255980");
        metaData.setFormat("NVD_CVE");

        dao.updateMetaData(metaData);
    }
}
