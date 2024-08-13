package businessObjects;

import businessObjects.baseClasses.BaseResponse;
import businessObjects.cve.CveEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * Object representation of an NVD response
 * header fields inherited from base class
 */
@Getter
@Setter
public final class NvdResponse extends BaseResponse {
    private CveEntity entity;

    public NvdResponse(CveEntity entity, int status) {
       this.entity = entity;
       this.status = status;
    }
}

