package businessObjects.interfaces;
import businessObjects.baseClasses.BaseResponse;
import exceptions.ApiCallException;

public interface IRequest {
    BaseResponse executeRequest() throws ApiCallException;
}
