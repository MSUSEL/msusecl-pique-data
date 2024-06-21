package database.postgreSQL;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbcp2.BasicDataSource;

import common.DataProperties;

public class PostgresConnectionManager {
    private static BasicDataSource connectionPool = new BasicDataSource();
    private static Properties prop = initializeProperties(); 

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

    public static Connection getConnection() throws SQLException {
        return connectionPool.getConnection();
    }

    private PostgresConnectionManager() {}

    private static String buildConnectionString(String driver, String hostname, String port, String dbname) {
        String connStr = String.format("%s://%s:%s/%s", driver, hostname, port, dbname);
        return connStr;
    }

    private static Properties initializeProperties() {
        try {
            return DataProperties.getProperties("src/main/resources/postgres.properties");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}