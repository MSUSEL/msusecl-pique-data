package presentation;

import businessObjects.NvdRequestBuilder;
import businessObjects.cve.Cve;
import businessObjects.cve.CveEntity;
import businessObjects.cve.NvdMirrorMetaData;
import com.mongodb.client.MongoClient;
import common.Constants;
import handlers.IJsonMarshaller;
import handlers.JsonMarshallerFactory;
import handlers.JsonResponseHandler;
import persistence.IDao;
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
    private final IJsonMarshaller cveMarshaller = new JsonMarshallerFactory(Cve.class).getMarshaller();
    private final IJsonMarshaller cveEntityMarshaller = new JsonMarshallerFactory(CveEntity.class).getMarshaller();
    private final JsonResponseHandler jsonResponseHandler = new JsonResponseHandler();
    private final NvdApiService nvdApiService = new NvdApiService(jsonResponseHandler, cveEntityMarshaller);
    private final GhsaApiService ghsaApiService = new GhsaApiService(new GhsaResponseProcessor());
    private final CveResponseProcessor cveResponseProcessor = new CveResponseProcessor();
    private final IDataSource<Connection> pgDataSource = new PostgresConnectionManager();
    private final IDataSource<MongoClient> mongoDataSource = new MongoConnectionManager();
    private final IDao<Cve> postgresCveDao = new PostgresCveDao(pgDataSource, cveMarshaller);
    private final IDao<NvdMirrorMetaData> postgresMetaDataDao = new PostgresMetaDataDao(pgDataSource);
    private final String dbContext = System.getenv("DB_CONTEXT");

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

    public NvdRequestBuilder getNvdRequestBuilder() {
        return new NvdRequestBuilder(jsonResponseHandler, cveEntityMarshaller);
    }

    private NvdMirrorManager instantiateNvdMirrorManager() {
        return new NvdMirrorManager(
                cveResponseProcessor,
                jsonResponseHandler,
                cveMarshaller,
                postgresCveDao,
                postgresMetaDataDao);
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
                new MongoCveDao(mongoDataSource, cveEntityMarshaller),
                new MongoMetaDataDao(mongoDataSource));
    }
}
