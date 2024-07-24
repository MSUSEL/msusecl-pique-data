package businessObjects;

import businessObjects.baseClasses.BaseRequest;
import businessObjects.interfaces.HTTPMethod;
import businessObjects.interfaces.IRequest;
import common.Constants;
import common.HeaderBuilder;
import common.NvdConstants;
import common.ParameterBuilder;
import handlers.JsonResponseHandler;
import handlers.NvdCveMarshaller;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
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
public final class NvdRequest extends BaseRequest implements IRequest {
    private static final Logger LOGGER = LoggerFactory.getLogger(NvdRequest.class);

    private final String httpMethod;
    private String baseUri;
    private Header[] headers;
    private final List<NameValuePair> params;

    public NvdRequest(String httpMethod, String baseUri, Header[] headers, List<NameValuePair> params) {
        super(httpMethod, baseUri, headers);
        this.params = params;
    }

    /**
     * Executes the API request and handles the result
     * @return the requested NvdResponse object
     */
    @Override
    public NvdResponse executeRequest() {
        return executeGetRequest();
    }


    // if using this class as a template to extend Request functionality, overloaded constructors providing
    // options for POST/PUT etc. requests could go here. However, for the NVD, GET is likely all that will be offered.



    private NvdResponse executeGetRequest() {
        HttpGet request = new HttpGet();
        URI uri = buildUri();
        request.setURI(uri);
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

    private NvdResponse makeHttpCall(HttpGet request) {
        NvdResponse nvdResponse = new NvdResponse();

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(request)) {

            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                String json = new JsonResponseHandler().handleResponse(response);
                nvdResponse.setCveResponse(new NvdCveMarshaller().unmarshalJson(json));
                nvdResponse.setStatus(status);
            } else {
                LOGGER.info(Constants.RESPONSE_STATUS_MESSAGE, status);
                throw new IOException(Constants.REQUEST_EXECUTION_FAILURE_MESSAGE + response.getStatusLine());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return nvdResponse;
    }
}

