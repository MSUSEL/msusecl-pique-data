package presentation;

import com.google.gson.Gson;
import handlers.IJsonSerializer;
import handlers.JsonResponseHandler;
import handlers.JsonSerializer;
import handlers.SecurityAdvisoryMarshaller;
import persistence.IDataSource;
import persistence.postgreSQL.PostgresConnectionManager;
import persistence.postgreSQL.PostgresCveDao;
import persistence.postgreSQL.PostgresMetadataDao;
import service.*;

import java.sql.Connection;

public class PiqueDataFactory {
    private final JsonResponseHandler jsonResponseHandler = new JsonResponseHandler();
    private final IJsonSerializer jsonSerializer = new JsonSerializer(new Gson());
    private final GhsaApiService ghsaApiService = new GhsaApiService(new GhsaResponseProcessor(), new SecurityAdvisoryMarshaller(), jsonResponseHandler);
    private final CveResponseProcessor cveResponseProcessor = new CveResponseProcessor();
    private final IDataSource<Connection> pgDataSource;

    public PiqueDataFactory() {
        this.pgDataSource = new PostgresConnectionManager(new CredentialService());
    }

    public PiqueDataFactory(String credentialsFilePath) {
        this.pgDataSource = new PostgresConnectionManager(new CredentialService(credentialsFilePath));
    }

    public PiqueData getPiqueData() {
        return new PiqueData(
                new NvdApiService(jsonResponseHandler, jsonSerializer),
                ghsaApiService,
                instantiatePgMirrorService(),
                cveResponseProcessor);
    }

    public NvdMirror getNvdMirror() {
        return new NvdMirror(instantiatePgMirrorService(), instantiatePgNvdMirrorManager());
    }

    public NvdRequestBuilder getNvdRequestBuilder() {
        return new NvdRequestBuilder(jsonResponseHandler, jsonSerializer);
    }

    private NvdMirrorManager instantiatePgNvdMirrorManager() {
        return new NvdMirrorManager(
                cveResponseProcessor,
                jsonResponseHandler,
                jsonSerializer,
                new PostgresCveDao(pgDataSource, jsonSerializer),
                new PostgresMetadataDao(pgDataSource));
    }

    private MirrorService instantiatePgMirrorService() {
        return new MirrorService(
                cveResponseProcessor,
                new PostgresCveDao(pgDataSource, jsonSerializer),
                new PostgresMetadataDao(pgDataSource));
    }
}
