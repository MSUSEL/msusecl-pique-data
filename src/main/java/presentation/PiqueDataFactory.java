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
import handlers.IJsonSerializer;
import handlers.JsonResponseHandler;
import handlers.JsonSerializer;
import handlers.SecurityAdvisoryMarshaller;
import persistence.IDataSource;
import persistence.postgreSQL.Migration;
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
    private final NvdMirrorManager manager = instantiatePgNvdMirrorManager();
    private final INvdMirrorService nvdMirrorService = instantiatePgMirrorService();

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
                nvdMirrorService,
                cveResponseProcessor);
    }

    public NvdMirror getNvdMirror() {
        return new NvdMirror(
                nvdMirrorService,
                manager,
                new Migration(pgDataSource, manager, nvdMirrorService));
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
