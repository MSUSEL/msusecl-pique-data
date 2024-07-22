package businessObjects;

import businessObjects.baseClasses.BaseRequest;
import common.Constants;
import handlers.JsonResponseHandler;
import handlers.SecurityAdvisoryMarshaller;

import org.apache.http.Header;
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

public final class GHSARequest extends BaseRequest {
    private static final Logger LOGGER = LoggerFactory.getLogger(GHSARequest.class);
    private final JsonResponseHandler handler = new JsonResponseHandler();
    private final String query;

    public GHSARequest(String httpMethod, String baseURI, Header[] headers, String query) {
        super(httpMethod, baseURI, headers);
        this.query = query;
    }

    @Override
    public GHSAResponse executeRequest() {
        return executeGHSARequest();
    }

    // TODO fix the awkward error handling here
    private GHSAResponse executeGHSARequest() {
        URI uri;
        GHSAResponse ghsaResponse = new GHSAResponse();
        SecurityAdvisoryMarshaller securityAdvisoryMarshaler = new SecurityAdvisoryMarshaller();

        uri = buildUri();
        HttpPost request = formatRequest(uri);

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(request)) {

            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                String json = handler.handleResponse(response);
                ghsaResponse.setSecurityAdvisory(securityAdvisoryMarshaler.unmarshalJson(json));
                ghsaResponse.setStatus(status);
            } else {
                LOGGER.info(Constants.RESPONSE_STATUS_MESSAGE, status);
                throw new IOException(Constants.REQUEST_EXECUTION_FAILURE_MESSAGE + response.getStatusLine());
            }
        } catch ( IOException e) {
            throw new RuntimeException(e);
        }

        return ghsaResponse;
    }

    private URI buildUri() {
        try {
            return new URIBuilder(baseURI).build();
        } catch (URISyntaxException e) {
            LOGGER.error(Constants.URI_BUILD_FAILURE_MESSAGE, e);
            throw new RuntimeException(e);
        }
    }

    private HttpPost formatRequest(URI uri) {
        HttpPost request = new HttpPost();
        request.setURI(uri);
        request.setHeaders(headers);
        request.setEntity(new StringEntity(query, StandardCharsets.UTF_8));

        return request;
    }
}
