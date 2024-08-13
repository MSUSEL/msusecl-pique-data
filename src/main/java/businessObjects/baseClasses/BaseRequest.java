package businessObjects.baseClasses;

import businessObjects.IRequest;
import exceptions.ApiCallException;
import org.apache.http.Header;
import org.apache.http.NameValuePair;

import java.util.List;

/**
 * Base class for any HTTP Request objects
 */
public abstract class BaseRequest implements IRequest {
    protected String httpMethod;
    protected String baseUri;
    protected Header[] headers;
    protected List<NameValuePair> params;

    public BaseRequest(String httpMethod, String baseURI, Header[] headers, List<NameValuePair> params) {
        this.httpMethod = httpMethod;
        this.baseUri = baseURI;
        this.headers = headers;
        this.params = params;
    }

    public BaseRequest(String httpMethod, String baseUri, Header[] headers) {
        this.httpMethod = httpMethod;
        this.baseUri = baseUri;
        this.headers = headers;
    }

    @Override
    public abstract BaseResponse executeRequest() throws ApiCallException;
}
