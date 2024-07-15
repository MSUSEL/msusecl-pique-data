package persistence;

import java.sql.Connection;

public interface IDataSource<T> {
    T getConnection();
}
