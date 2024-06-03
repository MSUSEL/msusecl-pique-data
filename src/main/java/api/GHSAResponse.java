package api;

import api.baseClasses.BaseResponse;
import api.ghsaData.SecurityAdvisory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GHSAResponse extends BaseResponse {
    private SecurityAdvisory securityAdvisory;
}
