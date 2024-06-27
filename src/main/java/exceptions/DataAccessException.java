package exceptions;

public class DataAccessException extends Exception{
    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Exception e) {
        super(message, e);
    }
}
