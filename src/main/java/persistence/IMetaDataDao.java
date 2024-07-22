package persistence;

import exceptions.DataAccessException;

public interface IMetaDataDao<T> {
    boolean updateMetaData(T metaData) throws DataAccessException;
    T fetchMetaData() throws DataAccessException;
}
