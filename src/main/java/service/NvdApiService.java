package service;

import businessObjects.NvdRequestBuilder;
import businessObjects.NvdRequest;
import businessObjects.cve.CVEResponse;
import businessObjects.cve.Cve;
import common.*;
import exceptions.ApiCallException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NvdApiService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NvdApiService.class);
    private final CveResponseProcessor cveResponseProcessor = new CveResponseProcessor();

    /**
     * Calls to NVD CVE2.0 API filtering results to single CVE
     * @param cveId the cveId of the CVE in question
     * @return Cve object from NVD response
     */
    public Cve handleGetCveFromNvd(String cveId) throws ApiCallException {
        return cveResponseProcessor.extractSingleCve(
                performApiCall(new NvdRequestBuilder().withApiKey(Constants.NVD_API_KEY).withCveId(cveId).build()));
    }

    public CVEResponse performApiCall(NvdRequest request) throws ApiCallException {
        return request.executeRequest().getCveResponse();
    }
}
