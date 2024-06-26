package businessObjects;

import businessObjects.baseClasses.BaseResponse;
import businessObjects.cveData.CVEResponse;
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

