package persistence.postgreSQL;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbcp2.BasicDataSource;

import common.DataUtilityProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PostgresConnectionManager {
    private static final BasicDataSource connectionPool = new BasicDataSource();
    private static final Properties prop = initializeProperties();
    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresConnectionManager.class);

    static {
        connectionPool.setUrl(
                buildConnectionString(
                        prop.getProperty("driver"),
                        prop.getProperty("hostname"),
                        prop.getProperty("port"),
                        prop.getProperty("dbname")));
        connectionPool.setUsername(prop.getProperty("username"));
        connectionPool.setPassword(prop.getProperty("password"));
        connectionPool.setMinIdle(5);
        connectionPool.setMaxIdle(10);
        connectionPool.setMaxOpenPreparedStatements(100);
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

    private static Properties initializeProperties() {
        try {
            return DataUtilityProperties.getProperties("src/main/resources/postgres.properties");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}