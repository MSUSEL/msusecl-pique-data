package persistence;

import exceptions.DataAccessException;

import java.util.List;

/**
 * Contract to perform CRUD operations
 * @param <T>
 */
public interface IDao<T> {
    List<T> fetch(List<String> ids) throws DataAccessException;
    void insert(List<T> t) throws DataAccessException;
    void update(List<T> t) throws DataAccessException;
    void delete(List<String> t) throws DataAccessException;
}
