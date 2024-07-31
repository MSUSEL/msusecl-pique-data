package presentation;

import businessObjects.cve.Cve;
import com.mongodb.client.MongoClient;
import common.HeaderBuilder;
import handlers.CveMarshaller;
import handlers.IJsonMarshaller;
import persistence.IDataSource;
import persistence.mongo.MongoBulkCveDao;
import persistence.mongo.MongoConnectionManager;
import persistence.mongo.MongoCveDao;
import persistence.mongo.MongoMetaDataDao;
import persistence.postgreSQL.PostgresBulkCveDao;
import persistence.postgreSQL.PostgresConnectionManager;
import persistence.postgreSQL.PostgresCveDao;
import persistence.postgreSQL.PostgresMetaDataDao;
import service.*;

import java.sql.Connection;

public class PiqueDataFactory {
    private final INvdMirrorService mirrorService = new MirrorService();
    private final NvdApiService nvdApiService = new NvdApiService();
    private final GhsaApiService ghsaApiService = new GhsaApiService(new HeaderBuilder());
    private final CveResponseProcessor cveResponseProcessor = new CveResponseProcessor();
    private final IDataSource<Connection> pgDataSource = new PostgresConnectionManager();
    private final IDataSource<MongoClient> mongoDataSource = new MongoConnectionManager();
    private final String dbContext = System.getenv("DB_CONTEXT");
    private final IJsonMarshaller<Cve> cveMarshaller = new CveMarshaller();

    public PiqueData getPiqueData() {
        if (dbContext.equals("persistent")) {
            return new PiqueData(
                    nvdApiService,
                    ghsaApiService,
                    new MirrorService(
                            cveResponseProcessor,
                            new PostgresCveDao(pgDataSource, cveMarshaller),
                            new PostgresBulkCveDao(pgDataSource, cveMarshaller),
                            new PostgresMetaDataDao(pgDataSource)),
                    // TODO only pass the cveResponse processor once
                    cveResponseProcessor);

        } else if (dbContext.equals("local")) {
            return new PiqueData(
                    nvdApiService,
                    ghsaApiService,
                    new MirrorService(
                            cveResponseProcessor,
                            new MongoCveDao(mongoDataSource, cveMarshaller),
                            new MongoBulkCveDao(mongoDataSource, cveMarshaller),
                            new MongoMetaDataDao(mongoDataSource)

                    )
            )
        }
    }

    private IDataSource getDataSource(String dbContext) {

    }
}
