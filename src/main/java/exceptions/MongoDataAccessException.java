package exceptions;

public class MongoDataAccessException extends DataAccessException {
    public MongoDataAccessException(String message) {
        super(message);
    }
}
