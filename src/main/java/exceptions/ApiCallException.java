package exceptions;

import lombok.Getter;

import java.sql.SQLException;

@Getter
public class ApiCallException extends Exception {
    private int errorCode = 0;

    public ApiCallException(int errorCode) {
        super("API call failed with error code: " + errorCode);
        this.errorCode = errorCode;
    }

    public ApiCallException(Exception e) {
        super(e);
    }

    public ApiCallException (String message) {
        super(message);
    }
}
