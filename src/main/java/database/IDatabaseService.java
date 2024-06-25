package database;

public interface IDatabaseService<T> {
    T getConnection();
}
