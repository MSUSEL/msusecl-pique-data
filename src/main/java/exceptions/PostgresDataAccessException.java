package exceptions;

public class PostgresDataAccessException extends DataAccessException{
    public PostgresDataAccessException(String message) {
        super(message);
    }
}
