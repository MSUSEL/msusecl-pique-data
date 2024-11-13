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
