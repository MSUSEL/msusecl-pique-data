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

import businessObjects.*;
import businessObjects.ghsa.Nodes;
import businessObjects.ghsa.SecurityAdvisory;
import businessObjects.ghsa.WebPiqueSecurityAdvisory;
import common.Constants;
import exceptions.ApiCallException;
import handlers.JsonResponseHandler;
import handlers.SecurityAdvisoryMarshaller;
import handlers.WebPiqueSecurityAdvisoryMarshaller;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.HeaderBuilder;

import java.util.ArrayList;
import java.util.List;

public class WebPiqueGhsaApiService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebPiqueGhsaApiService.class);
    private final WebPiqueSecurityAdvisoryMarshaller marshaller;
    private final JsonResponseHandler responseHandler;

    public WebPiqueGhsaApiService(WebPiqueSecurityAdvisoryMarshaller marshaller, JsonResponseHandler responseHandler) {
        this.marshaller = marshaller;
        this.responseHandler = responseHandler;
    }

    public WebPiqueSecurityAdvisory handleGetEntity(String ghsaId) throws ApiCallException {
        String CONTENT_TYPE = "Content-Type";
        String APP_JSON = "application/json";
        String AUTHORIZATION = "Authorization";

        WebPiqueGHSARequest ghsaRequest = new WebPiqueGHSARequest(
                HTTPMethod.POST,
                Constants.GHSA_URI,
                new HeaderBuilder()
                        .addHeader(CONTENT_TYPE, APP_JSON)
                        .addHeader(AUTHORIZATION, String.format("Bearer %s", System.getenv("GITHUB_PAT")))
                        .build(),
                formatQueryBody(ghsaId),
                marshaller,
                responseHandler);
        WebPiqueGHSAResponse webPiqueGHSAResponse = ghsaRequest.executeRequest();

        int status = webPiqueGHSAResponse.getStatus();
        if (status >= 200 && status < 300) {
            return webPiqueGHSAResponse.getEntity();
        } else {
            throw new ApiCallException(status);
        }
    }

    public List<String> handleGetCweIdsFromGhsa(String ghsaId) throws ApiCallException {
        WebPiqueSecurityAdvisory webPiqueSecurityAdvisory = handleGetEntity(ghsaId);
        List<String> cwes = new ArrayList<>();
        
        if (webPiqueSecurityAdvisory == null || webPiqueSecurityAdvisory.getCwes() == null) {
            LOGGER.warn("No CWE data available for GHSA ID: {}", ghsaId);
            return cwes;
        }
        
        if (webPiqueSecurityAdvisory.getCwes().getNodes() == null) {
            LOGGER.warn("CWE nodes are null for GHSA ID: {}", ghsaId);
            return cwes;
        }
        
        for (Nodes node : webPiqueSecurityAdvisory.getCwes().getNodes()){
            cwes.add(node.getCweId());
        }
        return cwes;
    }

    // TODO replace the following methods with dedicated GraphQL library
    private String formatQueryBody(String ghsaId) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("query", GraphQlQueries.GHSA_WEB_PIQUE_SECURITY_ADVISORY_QUERY);
            String query = jsonBody.toString();
            return String.format(query, ghsaId);
        } catch (JSONException e) {
            LOGGER.error("Improper JSON formatting. Check query format. ", e);
            throw new RuntimeException(e);
        }
    }
}
