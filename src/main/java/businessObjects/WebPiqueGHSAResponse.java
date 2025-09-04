package businessObjects;

import businessObjects.baseClasses.BaseResponse;
import businessObjects.ghsa.WebPiqueSecurityAdvisory;
import exceptions.ApiCallException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class WebPiqueGHSAResponse extends BaseResponse {

    private WebPiqueSecurityAdvisory entity;

    public WebPiqueGHSAResponse(WebPiqueSecurityAdvisory entity, int status) throws ApiCallException {
        this.entity = entity;
        this.status = status;
    }

}
