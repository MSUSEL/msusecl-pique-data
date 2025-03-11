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
package service;

import handlers.INvdSerializer;
import presentation.NvdRequestBuilder;
import businessObjects.cve.CveEntity;
import common.*;
import exceptions.ApiCallException;
import org.apache.http.client.ResponseHandler;

public final class NvdApiService {
    private final ResponseHandler<String> jsonResponseHandler;
    private final INvdSerializer serializer;

    public NvdApiService(ResponseHandler<String> jsonResponseHandler, INvdSerializer serializer) {
        this.jsonResponseHandler = jsonResponseHandler;
        this.serializer = serializer;
    }

    /**
     * Calls to NVD CVE2.0 API for a CveEntity (metadata + list of vulnerabilities)
     * @param id the cveId of the CVE in question
     * @return Cve object from NVD response
     */
    public CveEntity handleGetEntity(String id) throws ApiCallException {
        return new NvdRequestBuilder(jsonResponseHandler, serializer)
                .withApiKey(Constants.NVD_API_KEY)
                .withCveId(id)
                .build().executeRequest().getEntity();
    }
}
