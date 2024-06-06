package api;

import api.baseClasses.BaseResponse;
import api.cveData.CVEResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * Object representation of an NVD response
 * header fields inherited from base class
 */
@Getter
@Setter
public class NVDResponse extends BaseResponse {
    private CVEResponse cveResponse;
}

