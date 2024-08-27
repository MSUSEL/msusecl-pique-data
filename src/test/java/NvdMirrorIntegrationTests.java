import businessObjects.cve.Cve;
import businessObjects.cve.NvdMirrorMetaData;
import common.Constants;
import exceptions.ApiCallException;
import exceptions.DataAccessException;
import org.junit.Test;
import persistence.IDao;
import persistence.IDataSource;
import persistence.postgreSQL.PostgresConnectionManager;
import persistence.postgreSQL.PostgresMetaDataDao;
import presentation.NvdMirror;
import presentation.PiqueData;
import presentation.PiqueDataFactory;
import service.CredentialService;

import java.nio.file.Paths;
import java.sql.Connection;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 * IMPORTANT!
 * Some of these currently mutate the production database
 * Only run these if you know exactly what you're doing!
 */

// TODO mock database to test methods
public class NvdMirrorIntegrationTests {
    private final PiqueDataFactory piqueDataFactory = new PiqueDataFactory();
    private final PiqueData piqueData = piqueDataFactory.getPiqueData();
    private final NvdMirror nvdMirror = piqueDataFactory.getNvdMirror();

    @Test
    public void testBuildLocalNvdMirror() throws DataAccessException, ApiCallException {
        nvdMirror.buildNvdMirror();
    }

    @Test
    public void testBuildPersistentNvdMirror() throws DataAccessException, ApiCallException {
        nvdMirror.buildNvdMirror();
    }

    @Test
    public void testUpdateLocalNvdMirror() throws DataAccessException, ApiCallException {
        nvdMirror.updateNvdMirror();
    }

    @Test
    public void testUpdatePersistent() throws DataAccessException, ApiCallException {
        nvdMirror.updateNvdMirror();
    }

    @Test
    public void testGetLocalMetaData() throws DataAccessException {
        NvdMirrorMetaData metaData = nvdMirror.getMetaData();
    }

    @Test
    public void testGetPersistentMetaData() throws DataAccessException {
        NvdMirrorMetaData metaData = nvdMirror.getMetaData();
        System.out.println(metaData.getId());
        System.out.println(metaData.getTotalResults());
        System.out.println(metaData.getFormat());
        System.out.println(metaData.getVersion());
        System.out.println(metaData.getTimestamp());
    }

    @Test
    public void testLocalInsertSingleCve() throws DataAccessException {
        // TODO replace this with mocked Cve object
        Cve cve = piqueData.getCve(TestConstants.CVE_A);
        nvdMirror.insertSingleCve(cve);
    }

    @Test
    public void testPersistentInsertSingleCve() throws DataAccessException {
        // TODO replace this with mocked Cve object
        Cve cve = piqueData.getCve(TestConstants.CVE_A);
        nvdMirror.insertSingleCve(cve);
    }

    @Test
    public void testGetCveFromMirror() throws DataAccessException {
        Cve cve = piqueData.getCve(TestConstants.CVE_A);
        System.out.println(cve.getId());
    }

    @Test
    public void testInsertMetaData() throws DataAccessException {
        IDataSource<Connection> dataSource = new PostgresConnectionManager(new CredentialService());
        IDao<NvdMirrorMetaData> dao = new PostgresMetaDataDao(dataSource);
        NvdMirrorMetaData metaData = new NvdMirrorMetaData();
        metaData.setTimestamp("2024-07-07T23:26:08.260");
        metaData.setId("1");
        metaData.setVersion("2.0");
        metaData.setTotalResults("255980");
        metaData.setFormat("NVD_CVE");

        dao.update(Collections.singletonList(metaData));
    }

    @Test
    public void testDumpToFile() throws DataAccessException {
        nvdMirror.dumpNvdToFile("./out/nvd.json");
    }

    @Test
    public void testBuildMirrorFromJsonFile() throws DataAccessException {
        nvdMirror.buildMirrorFromJsonFile(Paths.get("./out/nvd.json"));

    }

   @Test
   public void testPiqueDataFactoryWithConstructorParams() throws DataAccessException {
        String file = "./src/main/resources/credentials.json";
        PiqueDataFactory piqueDataFactoryWithCreds = new PiqueDataFactory(file);
        PiqueData piqueDataWithCreds = piqueDataFactoryWithCreds.getPiqueData();

        Cve cve = piqueDataWithCreds.getCve(TestConstants.CVE_A);
        assertEquals(TestConstants.CVE_A, cve.getId());
   }
}
