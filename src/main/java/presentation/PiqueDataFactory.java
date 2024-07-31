package presentation;

import businessObjects.cve.Cve;
import com.mongodb.client.MongoClient;
import common.Constants;
import common.HeaderBuilder;
import handlers.CveMarshaller;
import handlers.IJsonMarshaller;
import persistence.IDataSource;
import persistence.mongo.MongoConnectionManager;
import persistence.mongo.MongoCveDao;
import persistence.mongo.MongoMetaDataDao;
import persistence.postgreSQL.PostgresConnectionManager;
import persistence.postgreSQL.PostgresCveDao;
import persistence.postgreSQL.PostgresMetaDataDao;
import service.*;

import java.sql.Connection;

public class PiqueDataFactory {
    private final NvdApiService nvdApiService = new NvdApiService();
    private final GhsaApiService ghsaApiService = new GhsaApiService(new HeaderBuilder());
    private final CveResponseProcessor cveResponseProcessor = new CveResponseProcessor();
    private final IDataSource<Connection> pgDataSource = new PostgresConnectionManager();
    private final IDataSource<MongoClient> mongoDataSource = new MongoConnectionManager();
    private final String dbContext = System.getenv("DB_CONTEXT");
    private final IJsonMarshaller<Cve> cveMarshaller = new CveMarshaller();

    public PiqueData getPiqueData() {
        PiqueData piqueData;
        if (dbContext.equals(Constants.DB_CONTEXT_PERSISTENT)) {
            piqueData =  new PiqueData(
                    nvdApiService,
                    ghsaApiService,
                    instantiatePgMirrorService(),
                    cveResponseProcessor);

        } else if (dbContext.equals(Constants.DB_CONTEXT_LOCAL)) {
            piqueData = new PiqueData(
                    nvdApiService,
                    ghsaApiService,
                    instantiateMongoMirrorService(),
                    cveResponseProcessor);
        } else {
            throw new IllegalArgumentException(Constants.DB_CONTEXT_ENV_VAR_ERROR_MESSAGE);
        }

        return piqueData;
    }

    public NvdMirror getNvdMirror() {
        NvdMirror nvdMirror;
        if (dbContext.equals(Constants.DB_CONTEXT_PERSISTENT)) {
            nvdMirror = new NvdMirror(instantiatePgMirrorService(), instantiateNvdMirrorManager());
        } else if (dbContext.equals(Constants.DB_CONTEXT_LOCAL)) {
            nvdMirror = new NvdMirror(instantiateMongoMirrorService(), instantiateNvdMirrorManager());
        } else {
            throw new IllegalArgumentException(Constants.DB_CONTEXT_ENV_VAR_ERROR_MESSAGE);
        }

        return nvdMirror;
    }

    private NvdMirrorManager instantiateNvdMirrorManager() {
        return new NvdMirrorManager(nvdApiService, cveResponseProcessor, cveMarshaller);
    }

    private MirrorService instantiatePgMirrorService() {
        return new MirrorService(
                cveResponseProcessor,
                new PostgresCveDao(pgDataSource, cveMarshaller),
                new PostgresMetaDataDao(pgDataSource));
    }

    private MirrorService instantiateMongoMirrorService() {
        return new MirrorService(
                cveResponseProcessor,
                new MongoCveDao(mongoDataSource, cveMarshaller),
                new MongoMetaDataDao(mongoDataSource));
    }
}
