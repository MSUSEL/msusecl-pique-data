package persistence.postgreSQL;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class PostgresConnectionManager {
    private static final BasicDataSource connectionPool = new BasicDataSource();
    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresConnectionManager.class);

    static {
        connectionPool.setUrl(
                buildConnectionString(
                        System.getenv("PG_DRIVER"),
                        System.getenv("PG_HOSTNAME"),
                        System.getenv("PG_PORT"),
                        System.getenv("PG_DBNAME")));
        connectionPool.setUsername(System.getenv("PG_USERNAME"));
        connectionPool.setPassword(System.getenv("PG_PASS"));
    }

    public static Connection getConnection() {
        try {
            return connectionPool.getConnection();
        } catch (SQLException e) {
            LOGGER.error("Connection to postgres failed. ", e);
            throw new RuntimeException(e);
        }
    }

    private PostgresConnectionManager() {}

    private static String buildConnectionString(String driver, String hostname, String port, String dbname) {
        return String.format("%s://%s:%s/%s", driver, hostname, port, dbname);
    }
}