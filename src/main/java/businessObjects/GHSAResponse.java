package businessObjects;

import businessObjects.baseClasses.BaseResponse;
import businessObjects.ghsa.SecurityAdvisory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class GHSAResponse extends BaseResponse {
    private SecurityAdvisory securityAdvisory;
}
