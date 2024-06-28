package persistence;

import exceptions.DataAccessException;

/**
 * Contract to perform CRUD operations
 * @param <T>
 */
public interface IDao<T> {
    T fetchById(String id) throws DataAccessException;
    void insert(T t) throws DataAccessException;
    void update(T t) throws DataAccessException;
    void delete(T t) throws DataAccessException;
}
