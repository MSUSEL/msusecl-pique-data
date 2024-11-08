package service;

import handlers.IJsonSerializer;
import handlers.JsonSerializer;
import presentation.NvdRequestBuilder;
import businessObjects.cve.CveEntity;
import common.*;
import exceptions.ApiCallException;
import org.apache.http.client.ResponseHandler;

public final class NvdApiService {
    private final ResponseHandler<String> jsonResponseHandler;
    private final IJsonSerializer serializer;

    public NvdApiService(ResponseHandler<String> jsonResponseHandler, IJsonSerializer serializer) {
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
