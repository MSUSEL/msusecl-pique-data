/*
 * MIT License
 *
 * Copyright (c) 2024 Montana State University Software Engineering and Cybersecurity Laboratory
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import businessObjects.cve.Cve;
import businessObjects.cve.NvdMirrorMetaData;
import com.google.gson.Gson;
import exceptions.ApiCallException;
import exceptions.DataAccessException;
import handlers.IJsonSerializer;
import handlers.JsonResponseHandler;
import handlers.JsonSerializer;
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
        PiqueDataFactory piqueDataFactory = new PiqueDataFactory(DEFAULT_CREDENTIALS_FILE_PATH);
        NvdMirror nvdMirror = piqueDataFactory.getNvdMirror();
        nvdMirror.updateNvdMirror();
    }

    @Test
    public void testGetLocalMetaData() throws DataAccessException {
        PiqueDataFactory piqueDataFactory = new PiqueDataFactory(DEFAULT_CREDENTIALS_FILE_PATH);
        NvdMirror nvdMirror = piqueDataFactory.getNvdMirror();
        NvdMirrorMetaData metaData = nvdMirror.getMetaData();
    }

    @Test
    public void testGetMetaData() throws DataAccessException {
        PiqueDataFactory piqueDataFactory = new PiqueDataFactory();
        NvdMirror nvdMirror = piqueDataFactory.getNvdMirror();

        NvdMirrorMetaData metaData = nvdMirror.getMetaData();

        System.out.println(metaData.getCvesModified());
        System.out.println(metaData.getFormat());
        System.out.println(metaData.getApiVersion());
        System.out.println(metaData.getLastTimestamp());
    }

    @Test
    public void testInsertSingleCve() throws DataAccessException {
        PiqueDataFactory piqueDataFactory = new PiqueDataFactory(DEFAULT_CREDENTIALS_FILE_PATH);
        PiqueData piqueData = piqueDataFactory.getPiqueData();
        NvdMirror nvdMirror = piqueDataFactory.getNvdMirror();

        Cve cve = piqueData.getCveFromNvd(TestConstants.CVE_A);
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
        PostgresMetadataDao dao = new PostgresMetadataDao(dataSource);
        NvdMirrorMetaData metaData = new NvdMirrorMetaData();
        metaData.setLastTimestamp("2024-09-07T23:26:08.260");
        metaData.setApiVersion("2.0");
        metaData.setCvesModified("255980");
        metaData.setFormat("NVD_CVE");

        dao.upsert(Collections.singletonList(metaData));
    }

   @Test
   public void testPiqueDataFactoryWithConstructorParams() throws DataAccessException {
        PiqueDataFactory piqueDataFactoryWithCreds = new PiqueDataFactory(DEFAULT_CREDENTIALS_FILE_PATH);
        PiqueData piqueDataWithCreds = piqueDataFactoryWithCreds.getPiqueData();

        Cve cve = piqueDataWithCreds.getCve(TestConstants.CVE_A);
        assertEquals(TestConstants.CVE_A, cve.getId());
   }

    @Test
    public void testDBSetup() {
        IJsonSerializer serializer = new JsonSerializer(new Gson());
        IDataSource<Connection> dataSource = new PostgresConnectionManager(
                        new CredentialService(DEFAULT_CREDENTIALS_FILE_PATH));
        IDao<Cve> postgresCveDao = new PostgresCveDao(dataSource, serializer);
        CveResponseProcessor cveResponseProcessor = new CveResponseProcessor();
        PostgresMetadataDao postgresMetaDataDao = new PostgresMetadataDao(dataSource);
        INvdMirrorService mirrorService = new MirrorService(cveResponseProcessor, postgresCveDao, postgresMetaDataDao);

        Migration migration = new Migration(
                dataSource,
                new NvdMirrorManager(
                        cveResponseProcessor,
                        new JsonResponseHandler(),
                        serializer,
                        postgresCveDao,
                        postgresMetaDataDao),
                mirrorService);

        migration.migrate();
    }

    @Test
    public void testBuildAndHydrateMirror() throws DataAccessException, ApiCallException {
        PiqueDataFactory piqueDataFactory = new PiqueDataFactory(DEFAULT_CREDENTIALS_FILE_PATH);
        NvdMirror nvdMirror = piqueDataFactory.getNvdMirror();
        nvdMirror.buildAndHydrateMirror();
    }
}
