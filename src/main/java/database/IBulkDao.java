package database;

public interface IBulkDao<T> {

    void insertMany(T entity);
}
