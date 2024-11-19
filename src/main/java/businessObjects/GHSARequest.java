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
package businessObjects;

import businessObjects.baseClasses.BaseRequest;
import businessObjects.ghsa.SecurityAdvisory;
import exceptions.ApiCallException;
import handlers.JsonResponseHandler;
import handlers.SecurityAdvisoryMarshaller;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import static common.Constants.*;

public final class GHSARequest extends BaseRequest implements IRequest {
    private static final Logger LOGGER = LoggerFactory.getLogger(GHSARequest.class);
    private final JsonResponseHandler handler;
    private final String query;
    private final SecurityAdvisoryMarshaller marshaller;

    public GHSARequest(String httpMethod, String baseURI, Header[] headers, String query, SecurityAdvisoryMarshaller marshaller, JsonResponseHandler jsonResponseHandler) {
        super(httpMethod, baseURI, headers);
        this.query = query;
        this.marshaller = marshaller;
        this.handler = jsonResponseHandler;
    }

    @Override
    public GHSAResponse executeRequest() throws ApiCallException {
        return executeGHSARequest();
    }

    private GHSAResponse executeGHSARequest() throws ApiCallException {
        return makeHttpCall(formatRequest(buildUri()));
    }

    private URI buildUri() {
        try {
            return new URIBuilder(baseUri).build();
        } catch (URISyntaxException e) {
            LOGGER.error(URI_BUILD_FAILURE_MESSAGE, e);
            throw new RuntimeException(e);
        }
    }

    private GHSAResponse makeHttpCall(HttpPost request) throws ApiCallException {
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(request)) {

            return processHttpResponse(response);

        } catch ( IOException e) {
            throw new ApiCallException(e);
        }
    }

    private GHSAResponse processHttpResponse(HttpResponse response) throws IOException {
        int status = response.getStatusLine().getStatusCode();
        if (status >= 200 && status < 300) {
            return new GHSAResponse(
                    marshaller.unmarshalJson(handler.handleResponse(response)),
                    status);
        } else {
            LOGGER.info(RESPONSE_STATUS_MESSAGE, status);
            throw new IOException(REQUEST_EXECUTION_FAILURE_MESSAGE + response.getStatusLine());
        }
    }

    private HttpPost formatRequest(URI uri) {
        HttpPost request = new HttpPost(uri);
        request.setHeaders(headers);
        request.setEntity(new StringEntity(query, StandardCharsets.UTF_8));

        return request;
    }
}
