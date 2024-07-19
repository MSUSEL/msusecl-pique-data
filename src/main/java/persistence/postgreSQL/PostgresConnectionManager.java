package persistence.postgreSQL;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.IDataSource;


public final class PostgresConnectionManager implements IDataSource<Connection> {
    private static final BasicDataSource connectionPool = new BasicDataSource();
    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresConnectionManager.class);

    public PostgresConnectionManager() {
        connectionPool.setUrl(
                buildConnectionString(
                        System.getenv("PG_DRIVER"),
                        System.getenv("PG_HOSTNAME"),
                        System.getenv("PG_PORT"),
                        System.getenv("PG_DBNAME")));
        connectionPool.setUsername(System.getenv("PG_USERNAME"));
        connectionPool.setPassword(System.getenv("PG_PASS"));
    }

    @Override
    public Connection getConnection() {
        try {
            return connectionPool.getConnection();
        } catch (SQLException e) {
            LOGGER.error("Connection to postgres failed. ", e);
            throw new RuntimeException(e);
        }
    }

    private static String buildConnectionString(String driver, String hostname, String port, String dbname) {
        return String.format("%s://%s:%s/%s", driver, hostname, port, dbname);
    }
}