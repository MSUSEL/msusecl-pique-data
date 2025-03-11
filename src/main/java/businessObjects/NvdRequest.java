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
import businessObjects.cve.CveEntity;
import common.Constants;
import exceptions.ApiCallException;

import handlers.INvdSerializer;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Inherits from Request and is used to execute GET requests against
 * the National Vulnerabilities Database
 */
public final class NvdRequest extends BaseRequest {
    private static final Logger LOGGER = LoggerFactory.getLogger(NvdRequest.class);
    private final ResponseHandler<String> jsonResponseHandler;
    private final INvdSerializer serializer;

    public NvdRequest(String httpMethod,
                      String baseUri,
                      Header[] headers,
                      List<NameValuePair> params,
                      ResponseHandler<String> jsonResponseHandler,
                      INvdSerializer serializer) {
        super(httpMethod, baseUri, headers, params);
        this.jsonResponseHandler = jsonResponseHandler;
        this.serializer = serializer;
    }

    /**
     * Executes the API request and handles the result
     * @return the requested NvdResponse object
     */
    @Override
    public NvdResponse executeRequest() throws ApiCallException {
        return executeGetRequest();
    }

    private NvdResponse executeGetRequest() throws ApiCallException {
        HttpGet request = new HttpGet();
        request.setURI(buildUri());
        request.setHeaders(headers);

        return makeHttpCall(request);
    }

    private URI buildUri() {
        try {
            return new URIBuilder(baseUri).addParameters(params).build();
        } catch (URISyntaxException e) {
            LOGGER.error(Constants.URI_BUILD_FAILURE_MESSAGE, e);
            throw new RuntimeException(e);
        }
    }

    private NvdResponse makeHttpCall(HttpGet request) throws ApiCallException {
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(request)) {

            return processHttpResponse(response);

        } catch (IOException e) {
            throw new ApiCallException(e);
        }
    }

    private NvdResponse processHttpResponse(CloseableHttpResponse response) throws IOException {
        int status = response.getStatusLine().getStatusCode();

        if (status >= 200 && status < 300) {
            return new NvdResponse(
                    serializer.deserialize(jsonResponseHandler.handleResponse(response), CveEntity.class),
                    status);
        } else {
            LOGGER.info(Constants.RESPONSE_STATUS_MESSAGE, status);
            throw new IOException(Constants.REQUEST_EXECUTION_FAILURE_MESSAGE + response.getStatusLine());
        }
    }
}

