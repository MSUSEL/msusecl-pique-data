package businessObjects;

import businessObjects.baseClasses.BaseResponse;
import businessObjects.ghsaData.SecurityAdvisory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GHSAResponse extends BaseResponse {
    private SecurityAdvisory securityAdvisory;
}
