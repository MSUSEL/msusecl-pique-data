package persistence.postgreSQL;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbcp2.BasicDataSource;

import common.DataUtilityProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.IDataSource;


public final class PostgresConnectionManager implements IDataSource<Connection> {
    private final BasicDataSource connectionPool = new BasicDataSource();
    private final Logger LOGGER = LoggerFactory.getLogger(PostgresConnectionManager.class);

    public PostgresConnectionManager(Properties prop) {
        connectionPool.setUrl(
                buildConnectionString(
                        prop.getProperty("driver"),
                        prop.getProperty("hostname"),
                        prop.getProperty("port"),
                        prop.getProperty("dbname")));
        connectionPool.setUsername(prop.getProperty("username"));
        connectionPool.setPassword(prop.getProperty("password"));
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

    private String buildConnectionString(String driver, String hostname, String port, String dbname) {
        return String.format("%s://%s:%s/%s", driver, hostname, port, dbname);
    }

    private Properties initializeProperties() {
        try {
            return DataUtilityProperties.getProperties("src/main/resources/postgres.properties");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}