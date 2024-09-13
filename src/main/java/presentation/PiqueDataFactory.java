package presentation;

import businessObjects.NvdRequestBuilder;
import businessObjects.cve.Cve;
import businessObjects.cve.CveEntity;
import businessObjects.ghsa.SecurityAdvisory;
import handlers.IJsonMarshaller;
import handlers.JsonMarshallerFactory;
import handlers.JsonResponseHandler;
import persistence.IDataSource;
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
    private final IDataSource<Connection> pgDataSource;

    public PiqueDataFactory() {
        this.pgDataSource = new PostgresConnectionManager(new CredentialService());
    }

    public PiqueDataFactory(String credentialsFilePath){
        CredentialService credentialService = new CredentialService(credentialsFilePath);
        this.pgDataSource = new PostgresConnectionManager(credentialService);
    }

    public PiqueData getPiqueData() {
        return new PiqueData(
                nvdApiService,
                ghsaApiService,
                instantiatePgMirrorService(),
                cveResponseProcessor);
    }

    public NvdMirror getNvdMirror() {
        return new NvdMirror(instantiatePgMirrorService(), instantiatePgNvdMirrorManager());
    }

    public NvdRequestBuilder getNvdRequestBuilder() {
        return new NvdRequestBuilder(jsonResponseHandler, cveEntityMarshaller);
    }

    private NvdMirrorManager instantiatePgNvdMirrorManager() {
        return new NvdMirrorManager(
                cveResponseProcessor,
                jsonResponseHandler,
                cveEntityMarshaller,
                new PostgresCveDao(pgDataSource, cveMarshaller),
                new PostgresMetaDataDao(pgDataSource));
    }

    private MirrorService instantiatePgMirrorService() {
        return new MirrorService(
                cveResponseProcessor,
                new PostgresCveDao(pgDataSource, cveMarshaller),
                new PostgresMetaDataDao(pgDataSource));
    }
}
