package persistence;

import exceptions.DataAccessException;

public interface IMetaDataDao<T> {
    T fetch() throws DataAccessException;
    void upsert(T data) throws DataAccessException;
}
