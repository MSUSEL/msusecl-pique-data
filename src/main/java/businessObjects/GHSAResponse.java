package businessObjects;

import businessObjects.baseClasses.BaseResponse;
import businessObjects.ghsa.SecurityAdvisory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class GHSAResponse extends BaseResponse {
    private final SecurityAdvisory entity;

    public GHSAResponse(SecurityAdvisory entity, int status) {
        this.entity = entity;
        this.status = status;
    }
}
