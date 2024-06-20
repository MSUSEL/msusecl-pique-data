package database.postgreSQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import database.IDatabaseConnection;

public class PostgresConnectionManager implements IDatabaseConnection<Connection> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresConnectionManager.class);

    public Connection getConnection(String driver, String hostname, String port, String dbname, String username, String password) {
        try (Connection connection = DriverManager.getConnection(
                buildConnectionString(driver, hostname, port, dbname),
                username,
                password)) {

            if (connection.isValid(10)) {
                LOGGER.info("Successfully connected to %s", dbname);
            } else {
                LOGGER.info("Failed to connect to %s", dbname);
                LOGGER.info("driver: %s, hostname: %s, port: %s", driver, hostname, port);
            }

            return connection;

        } catch (SQLException e) {
            LOGGER.error("Error connecting to database: " + e.getMessage());
            throw new RuntimeException(e);
        } 
    }

    private String buildConnectionString(String driver, String hostname, String port, String dbname) {
        String connStr = String.format("%s://%s:%s/%s", driver, hostname, port, dbname);
        return connStr;
    }
}