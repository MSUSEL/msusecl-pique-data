package service;

import businessObjects.GHSARequest;
import businessObjects.GHSAResponse;
import businessObjects.GraphQlQueries;
import businessObjects.interfaces.HTTPMethod;
import businessObjects.ghsa.SecurityAdvisory;
import common.Constants;
import common.HeaderBuilder;
import exceptions.ApiCallException;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;


public class GhsaApiService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GhsaApiService.class);
    private final HeaderBuilder headerBuilder = new HeaderBuilder();

    public SecurityAdvisory handleGetGhsa(String ghsaId) throws ApiCallException {
        String CONTENT_TYPE = "Content-Type";
        String APP_JSON = "application/json";
        String AUTHORIZATION = "Authorization";

        GHSARequest ghsaRequest = new GHSARequest(
                HTTPMethod.POST,
                Constants.GHSA_URI,
                headerBuilder.addHeader(CONTENT_TYPE, APP_JSON)
                        .addHeader(AUTHORIZATION, String.format("Bearer %s", System.getenv("NVD_KEY")))
                        .build(),
                formatQueryBody(ghsaId));
        GHSAResponse ghsaResponse = ghsaRequest.executeRequest();

        int status = ghsaResponse.getStatus();
        if (status == 200) {
            return ghsaResponse.getSecurityAdvisory();
        } else {
            throw new ApiCallException(status);
        }
    }

    public ArrayList<String> handleGetCweIdsFromGhsa(String ghsaId) throws ApiCallException {
        SecurityAdvisory advisory = handleGetGhsa(ghsaId);
        return new GhsaResponseProcessor().extractCweIds(advisory);
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
