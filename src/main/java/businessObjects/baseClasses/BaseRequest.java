package businessObjects.baseClasses;

import org.apache.http.Header;
import org.apache.http.NameValuePair;

import java.util.List;

/**
 * Base class for any HTTP Request objects
 */
public abstract class BaseRequest {
    protected String httpMethod;
    protected String baseURI;
    protected Header[] headers;
    protected List<NameValuePair> params;

    public BaseRequest(String httpMethod, String baseURI, Header[] headers) {
        this.httpMethod = httpMethod;
        this.baseURI = baseURI;
        this.headers = headers;
    }

    public abstract BaseResponse executeRequest();
}
