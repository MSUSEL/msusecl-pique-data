package exceptions;

import lombok.Getter;

@Getter
public class ApiCallException extends Exception {
    private final int errorCode;

    public ApiCallException(int errorCode) {
        super("API call failed with error code: " + errorCode);
        this.errorCode = errorCode;
    }
}
