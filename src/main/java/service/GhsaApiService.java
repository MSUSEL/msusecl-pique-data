package service;

import businessObjects.GHSARequest;
import businessObjects.GHSAResponse;
import businessObjects.GraphQlQueries;
import businessObjects.HTTPMethod;
import businessObjects.ghsa.SecurityAdvisory;
import common.DataUtilityProperties;
import common.Utils;
import exceptions.ApiCallException;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class GhsaApiService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GhsaApiService.class);
    private final Properties prop = DataUtilityProperties.getProperties();

    public SecurityAdvisory handleGetGhsa(String ghsaId) throws ApiCallException {
        GHSARequest ghsaRequest = new GHSARequest(HTTPMethod.POST, Utils.GHSA_URI, formatHeaders(), formatQueryBody(ghsaId));
        GHSAResponse ghsaResponse = ghsaRequest.executeRequest();

        int status = ghsaResponse.getStatus();
        if (status == 200) {
            return ghsaResponse.getSecurityAdvisory();
        } else {
            throw new ApiCallException(status);
        }
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

    private List<String> formatHeaders() {
        String githubToken = Utils.getAuthToken(prop.getProperty("github-token-path"));
        String authHeader = String.format("Bearer %s", githubToken);
        return Arrays.asList("Content-Type", "application/json", "Authorization", authHeader);
    }
}
