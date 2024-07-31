package service;

import businessObjects.GHSARequest;
import businessObjects.GHSAResponse;
import businessObjects.GraphQlQueries;
import businessObjects.interfaces.HTTPMethod;
import businessObjects.ghsa.SecurityAdvisory;
import common.Constants;
import common.HeaderBuilder;
import common.ParameterBuilder;
import exceptions.ApiCallException;
import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class GhsaApiService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GhsaApiService.class);
    private final HeaderBuilder headerBuilder;
    private final ParameterBuilder parameterBuilder;

    public GhsaApiService(HeaderBuilder headerBuilder, ParameterBuilder parameterBuilder) {
        this.headerBuilder = headerBuilder;
        this.parameterBuilder = parameterBuilder;
    }

    // TODO Fix params parameter (Yes I know that's confusing) with paramaterBuilder
    public SecurityAdvisory handleGetEntity(String ghsaId, List<NameValuePair> params) throws ApiCallException {
        String CONTENT_TYPE = "Content-Type";
        String APP_JSON = "application/json";
        String AUTHORIZATION = "Authorization";

        GHSARequest ghsaRequest = new GHSARequest(
                HTTPMethod.POST,
                Constants.GHSA_URI,
                headerBuilder.addHeader(CONTENT_TYPE, APP_JSON)
                        .addHeader(AUTHORIZATION, String.format("Bearer %s", Constants.NVD_API_KEY))
                        .build(),
                params,
                formatQueryBody(ghsaId));
        GHSAResponse ghsaResponse = ghsaRequest.executeRequest();

        int status = ghsaResponse.getStatus();
        if (status == 200) {
            return ghsaResponse.getEntity();
        } else {
            throw new ApiCallException(status);
        }
    }

    public ArrayList<String> handleGetCweIdsFromGhsa(String ghsaId) throws ApiCallException {
        SecurityAdvisory advisory = handleGetEntity(ghsaId);
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
