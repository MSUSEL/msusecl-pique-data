package businessObjects;

import businessObjects.baseClasses.BaseResponse;
import businessObjects.cve.CVEResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * Object representation of an NVD response
 * header fields inherited from base class
 */
@Getter
@Setter
public final class NvdResponse extends BaseResponse {
    private CVEResponse cveResponse;
}

