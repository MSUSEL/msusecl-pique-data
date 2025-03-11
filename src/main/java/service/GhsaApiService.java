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

import businessObjects.GHSARequest;
import businessObjects.GHSAResponse;
import businessObjects.GraphQlQueries;
import businessObjects.HTTPMethod;
import businessObjects.ghsa.SecurityAdvisory;
import common.Constants;
import handlers.IGhsaSerializer;
import handlers.JsonResponseHandler;
import persistence.HeaderBuilder;
import exceptions.ApiCallException;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GhsaApiService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GhsaApiService.class);
    private final GhsaResponseProcessor ghsaResponseProcessor;
    private final IGhsaSerializer<SecurityAdvisory> serializer;
    private final JsonResponseHandler responseHandler;

    public GhsaApiService(GhsaResponseProcessor ghsaResponseProcessor, IGhsaSerializer<SecurityAdvisory> serializer, JsonResponseHandler responseHandler) {
        this.ghsaResponseProcessor = ghsaResponseProcessor;
        this.serializer = serializer;
        this.responseHandler = responseHandler;
    }

    public SecurityAdvisory handleGetEntity(String ghsaId) throws ApiCallException {
        String CONTENT_TYPE = "Content-Type";
        String APP_JSON = "application/json";
        String AUTHORIZATION = "Authorization";

        GHSARequest ghsaRequest = new GHSARequest(
                HTTPMethod.POST,
                Constants.GHSA_URI,
                new HeaderBuilder()
                        .addHeader(CONTENT_TYPE, APP_JSON)
                        .addHeader(AUTHORIZATION, String.format("Bearer %s", System.getenv("GITHUB_PAT")))
                        .build(),
                formatQueryBody(ghsaId),
                serializer,
                responseHandler);
        GHSAResponse ghsaResponse = ghsaRequest.executeRequest();

        int status = ghsaResponse.getStatus();
        if (status >= 200 && status < 300) {
            return ghsaResponse.getEntity();
        } else {
            throw new ApiCallException(status);
        }
    }

    public List<String> handleGetCweIdsFromGhsa(String ghsaId) throws ApiCallException {
        SecurityAdvisory advisory = handleGetEntity(ghsaId);
        return ghsaResponseProcessor.extractCweIds(advisory);
    }

    // TODO replace the following methods with dedicated GraphQL library
    private String formatQueryBody(String ghsaId) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("query", GraphQlQueries.GHSA_SECURITY_ADVISORY_QUERY);
            String query = jsonBody.toString();
            return String.format(query, ghsaId);
        } catch (JSONException e) {
            LOGGER.error("Improper JSON formatting. Check query format. ", e);
            throw new RuntimeException(e);
        }
    }
}
