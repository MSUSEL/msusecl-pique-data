package presentation;

import businessObjects.NvdRequestBuilder;
import businessObjects.cve.Cve;
import businessObjects.cve.CveEntity;
import businessObjects.ghsa.SecurityAdvisory;
import com.mongodb.client.MongoClient;
import common.Constants;
import handlers.IJsonMarshaller;
import handlers.JsonMarshallerFactory;
import handlers.JsonResponseHandler;
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
    private final JsonMarshallerFactory jsonMarshallerFactory = new JsonMarshallerFactory();
    private final IJsonMarshaller<Cve> cveMarshaller = jsonMarshallerFactory.getCveMarshaller();
    private final IJsonMarshaller<CveEntity> cveEntityMarshaller = jsonMarshallerFactory.getCveEntityMarshaller();
    private final IJsonMarshaller<SecurityAdvisory> securityAdvisoryMarshaller = jsonMarshallerFactory.getSecurityAdvisoryMarshaller();
    private final JsonResponseHandler jsonResponseHandler = new JsonResponseHandler();
    private final NvdApiService nvdApiService = new NvdApiService(jsonResponseHandler, cveEntityMarshaller);
    private final GhsaApiService ghsaApiService = new GhsaApiService(new GhsaResponseProcessor(), securityAdvisoryMarshaller);
    private final CveResponseProcessor cveResponseProcessor = new CveResponseProcessor();
    private final IDataSource<MongoClient> mongoDataSource = new MongoConnectionManager();
    private final CredentialService credentialService = new CredentialService();
    private final String dbContext = credentialService.getDbContext();
    private IDataSource<Connection> pgDataSource;

    public PiqueDataFactory() {
        if (dbContext.equals(Constants.DB_CONTEXT_PERSISTENT)) {
            this.pgDataSource = new PostgresConnectionManager(credentialService);
        }
    }

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
            nvdMirror = new NvdMirror(instantiatePgMirrorService(), instantiatePgNvdMirrorManager());
        } else if (dbContext.equals(Constants.DB_CONTEXT_LOCAL)) {
            nvdMirror = new NvdMirror(instantiateMongoMirrorService(), instantiateMongoNvdMirrorManager());
        } else {
            throw new IllegalArgumentException(Constants.DB_CONTEXT_ENV_VAR_ERROR_MESSAGE);
        }

        return nvdMirror;
    }

    public NvdRequestBuilder getNvdRequestBuilder() {
        return new NvdRequestBuilder(jsonResponseHandler, cveEntityMarshaller);
    }

    private NvdMirrorManager instantiatePgNvdMirrorManager() {
        return new NvdMirrorManager(
                cveResponseProcessor,
                jsonResponseHandler,
                cveEntityMarshaller,
                new PostgresCveDao(pgDataSource, cveEntityMarshaller),
                new PostgresMetaDataDao(pgDataSource));
    }

    private NvdMirrorManager instantiateMongoNvdMirrorManager() {
        return new NvdMirrorManager(
                cveResponseProcessor,
                jsonResponseHandler,
                cveEntityMarshaller,
                new MongoCveDao(mongoDataSource, cveMarshaller),
                new MongoMetaDataDao(mongoDataSource));
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
