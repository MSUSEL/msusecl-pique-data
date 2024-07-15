package persistence;

import exceptions.DataAccessException;

import java.sql.SQLException;

public interface IExperimentalDao<T> {
    T fetchById(String id) throws DataAccessException, SQLException;
}
