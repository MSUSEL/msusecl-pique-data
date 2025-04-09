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
package presentation;

import com.google.gson.Gson;
import handlers.INvdSerializer;
import handlers.JsonResponseHandler;
import handlers.NvdSerializer;
import handlers.GhsaSerializer;
import org.apache.http.client.ResponseHandler;
import persistence.IDataSource;
import persistence.postgreSQL.PostgresConnectionManager;
import persistence.postgreSQL.PostgresCveDao;
import persistence.postgreSQL.PostgresMetadataDao;
import service.*;

import java.sql.Connection;

public class PiqueDataFactory {
    private final ResponseHandler<String> jsonResponseHandler = new JsonResponseHandler();
    private final INvdSerializer jsonSerializer = new NvdSerializer(new Gson());
    private final IGhsaApiService ghsaApiService = new GhsaApiService(new GhsaResponseProcessor(), new GhsaSerializer(), jsonResponseHandler);
    private final IResponseProcessor cveResponseProcessor = new CveResponseProcessor();
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
        NvdMirrorManager manager = instantiatePgNvdMirrorManager();
        INvdMirrorService nvdMirrorService = instantiatePgMirrorService();

        return new NvdMirror(
                nvdMirrorService,
                manager);
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
                new PostgresMetadataDao(pgDataSource),
                pgDataSource);
    }

    private MirrorService instantiatePgMirrorService() {
        return new MirrorService(
                cveResponseProcessor,
                new PostgresCveDao(pgDataSource, jsonSerializer),
                new PostgresMetadataDao(pgDataSource));
    }
}
