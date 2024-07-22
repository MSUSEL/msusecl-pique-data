package persistence;

import exceptions.DataAccessException;

import java.util.List;

public interface IBulkDao<T> {
    void insertMany(List<T> entity) throws DataAccessException;
    List<T> fetchMany(String[] entities)throws DataAccessException;
    T[] fetchAll()throws DataAccessException;
}
