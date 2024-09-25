package persistence.postgreSQL;

import persistence.IDataSource;

import java.sql.Connection;

public class PGInitializer {
    IDataSource<Connection> dataSource;

    public PGInitializer(IDataSource<Connection> dataSource) {
        this.dataSource = dataSource;
    }


}
