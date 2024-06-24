package database;

/**
 * Contract to perform CRUD operations
 * @param <T>
 */
public interface IDao<T> {
    T getById(String id);
    void insert(T t); 
    void update(T t);
    void delete(T t);
}
