package service;

import businessObjects.NvdRequestBuilder;
import businessObjects.cve.CveEntity;
import common.*;
import exceptions.ApiCallException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NvdApiService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NvdApiService.class);

    /**
     * Calls to NVD CVE2.0 API filtering results to single CVE
     * @param id the cveId of the CVE in question
     * @return Cve object from NVD response
     */
    public CveEntity handleGetEntity(String id) throws ApiCallException {
        return new NvdRequestBuilder()
                .withApiKey(Constants.NVD_API_KEY)
                .withCveId(id)
                .build()
                .executeRequest().getEntity();
    }
}
