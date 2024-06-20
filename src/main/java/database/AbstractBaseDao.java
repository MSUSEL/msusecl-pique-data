package database;

public abstract class AbstractBaseDao<T, U> implements IDao<T> {
    protected final IDatabaseConnection<U> databaseConnection = null;
}
