package service;

import businessObjects.NvdRequestBuilder;
import businessObjects.cve.CveEntity;
import common.*;
import exceptions.ApiCallException;
import handlers.IJsonMarshaller;
import org.apache.http.client.ResponseHandler;

public final class NvdApiService {
    private final ResponseHandler<String> jsonResponseHandler;
    private final IJsonMarshaller<CveEntity> cveEntityMarshaller;

    public NvdApiService(ResponseHandler<String> jsonResponseHandler, IJsonMarshaller<CveEntity> cveEntityMarshaller) {
        this.jsonResponseHandler = jsonResponseHandler;
        this.cveEntityMarshaller = cveEntityMarshaller;
    }

    /**
     * Calls to NVD CVE2.0 API for a CveEntity (metadata + list of vulnerabilities)
     * @param id the cveId of the CVE in question
     * @return Cve object from NVD response
     */
    public CveEntity handleGetEntity(String id) throws ApiCallException {
        return new NvdRequestBuilder(jsonResponseHandler, cveEntityMarshaller)
                .withApiKey(Constants.NVD_API_KEY)
                .withCveId(id)
                .build().executeRequest().getEntity();
    }
}
