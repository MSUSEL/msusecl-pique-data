package businessObjects;

import businessObjects.baseClasses.BaseRequest;
import businessObjects.ghsa.SecurityAdvisory;
import com.google.gson.Gson;
import common.Constants;
import exceptions.ApiCallException;
import handlers.IJsonMarshaller;
import handlers.JsonMarshallerFactory;
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

public final class GHSARequest extends BaseRequest implements IRequest {
    private static final Logger LOGGER = LoggerFactory.getLogger(GHSARequest.class);
    private final JsonResponseHandler handler = new JsonResponseHandler();
    private final String query;
    private final IJsonMarshaller<SecurityAdvisory> marshaller;

    public GHSARequest(String httpMethod, String baseURI, Header[] headers, String query, IJsonMarshaller<SecurityAdvisory> marshaller) {
        super(httpMethod, baseURI, headers);
        this.query = query;
        this.marshaller = marshaller;
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
            LOGGER.error(Constants.URI_BUILD_FAILURE_MESSAGE, e);
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
            LOGGER.info(Constants.RESPONSE_STATUS_MESSAGE, status);
            throw new IOException(Constants.REQUEST_EXECUTION_FAILURE_MESSAGE + response.getStatusLine());
        }
    }

    private HttpPost formatRequest(URI uri) {
        HttpPost request = new HttpPost(uri);
        request.setHeaders(headers);
        request.setEntity(new StringEntity(query, StandardCharsets.UTF_8));

        return request;
    }
}
