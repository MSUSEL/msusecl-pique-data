import businessObjects.cve.Cve;
import businessObjects.cve.CveEntity;
import businessObjects.cve.NvdMirrorMetaData;
import exceptions.ApiCallException;
import exceptions.DataAccessException;
import handlers.IJsonMarshaller;
import handlers.JsonMarshallerFactory;
import handlers.JsonResponseHandler;
import org.junit.Test;
import persistence.IDao;
import persistence.IDataSource;
import persistence.postgreSQL.Migration;
import persistence.postgreSQL.PostgresConnectionManager;
import persistence.postgreSQL.PostgresCveDao;
import persistence.postgreSQL.PostgresMetadataDao;
import presentation.NvdMirror;
import presentation.PiqueData;
import presentation.PiqueDataFactory;
import service.*;

import java.sql.Connection;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static common.Constants.*;

/**
 * IMPORTANT!
 * Some of these currently mutate the production database
 * Only run these if you know exactly what you're doing!
 */

// TODO mock database to test methods
public class NvdMirrorIntegrationTests {
    @Test
    public void testBuildNvdMirror() throws DataAccessException, ApiCallException {
        PiqueDataFactory piqueDataFactory = new PiqueDataFactory();
        NvdMirror nvdMirror = piqueDataFactory.getNvdMirror();
        nvdMirror.buildNvdMirror();
    }

    @Test
    public void testUpdateNvdMirror() throws DataAccessException, ApiCallException {
        PiqueDataFactory piqueDataFactory = new PiqueDataFactory();
        NvdMirror nvdMirror = piqueDataFactory.getNvdMirror();
        nvdMirror.updateNvdMirror();
    }

    @Test
    public void testGetLocalMetaData() throws DataAccessException {
        PiqueDataFactory piqueDataFactory = new PiqueDataFactory(CREDENTIALS_FILE_PATH);
        NvdMirror nvdMirror = piqueDataFactory.getNvdMirror();
        NvdMirrorMetaData metaData = nvdMirror.getMetaData();
    }

    @Test
    public void testGetMetaData() throws DataAccessException {
        PiqueDataFactory piqueDataFactory = new PiqueDataFactory();
        NvdMirror nvdMirror = piqueDataFactory.getNvdMirror();

        NvdMirrorMetaData metaData = nvdMirror.getMetaData();

        System.out.println(metaData.getTotalResults());
        System.out.println(metaData.getFormat());
        System.out.println(metaData.getVersion());
        System.out.println(metaData.getTimestamp());
    }

    @Test
    public void testInsertSingleCve() throws DataAccessException {
        // TODO replace this with mocked Cve object
        PiqueDataFactory piqueDataFactory = new PiqueDataFactory();
        PiqueData piqueData = piqueDataFactory.getPiqueData();
        NvdMirror nvdMirror = piqueDataFactory.getNvdMirror();

        Cve cve = piqueData.getCve(TestConstants.CVE_A);
        nvdMirror.insertSingleCve(cve);
    }

    @Test
    public void testGetCveFromMirror() throws DataAccessException {
        PiqueDataFactory piqueDataFactory = new PiqueDataFactory();
        PiqueData piqueData = piqueDataFactory.getPiqueData();

        Cve cve = piqueData.getCve(TestConstants.CVE_A);
        System.out.println(cve.getId());
    }

    @Test
    public void testInsertMetaData() throws DataAccessException {
        IDataSource<Connection> dataSource = new PostgresConnectionManager(new CredentialService());
        IDao<NvdMirrorMetaData> dao = new PostgresMetadataDao(dataSource);
        NvdMirrorMetaData metaData = new NvdMirrorMetaData();
        metaData.setTimestamp("2024-09-07T23:26:08.260");
        metaData.setVersion("2.0");
        metaData.setTotalResults("255980");
        metaData.setFormat("NVD_CVE");

        dao.upsert(Collections.singletonList(metaData));
    }

   @Test
   public void testPiqueDataFactoryWithConstructorParams() throws DataAccessException {
        PiqueDataFactory piqueDataFactoryWithCreds = new PiqueDataFactory(CREDENTIALS_FILE_PATH);
        PiqueData piqueDataWithCreds = piqueDataFactoryWithCreds.getPiqueData();

        Cve cve = piqueDataWithCreds.getCve(TestConstants.CVE_A);
        assertEquals(TestConstants.CVE_A, cve.getId());
   }

    @Test
    public void testDBSetup() {
        IDataSource<Connection> dataSource = new PostgresConnectionManager(
                        new CredentialService(CREDENTIALS_FILE_PATH));
        JsonMarshallerFactory jsonMarshallerFactory = new JsonMarshallerFactory();
        IDao<Cve> postgresCveDao = new PostgresCveDao(dataSource, jsonMarshallerFactory.getCveMarshaller());
        CveResponseProcessor cveResponseProcessor = new CveResponseProcessor();
        IDao<NvdMirrorMetaData> postgresMetaDataDao = new PostgresMetadataDao(dataSource);
        INvdMirrorService mirrorService = new MirrorService(cveResponseProcessor, postgresCveDao, postgresMetaDataDao);

        Migration migration = new Migration(
                dataSource,
                new NvdMirrorManager(
                        cveResponseProcessor,
                        new JsonResponseHandler(),
                        jsonMarshallerFactory.getCveEntityMarshaller(),
                        postgresCveDao,
                        postgresMetaDataDao),
                mirrorService);

        migration.migrate();
    }
}
