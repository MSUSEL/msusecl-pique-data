package persistence;

import exceptions.ApiCallException;
import exceptions.DataAccessException;

import java.sql.SQLException;

public interface IMetaDataDao<T> {
    boolean updateMetaData(T metaData) throws DataAccessException;
    T fetchMetaData() throws DataAccessException;
}
