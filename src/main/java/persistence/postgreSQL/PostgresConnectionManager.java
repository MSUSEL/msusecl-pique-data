package persistence.postgreSQL;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.IDataSource;
import service.CredentialService;


public final class PostgresConnectionManager implements IDataSource<Connection> {
    private final BasicDataSource connectionPool = new BasicDataSource();
    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresConnectionManager.class);

    public PostgresConnectionManager(CredentialService credentialService) {
        connectionPool.setUrl(
                buildConnectionString(
                        credentialService.getDriver(),
                        credentialService.getHostname(),
                        credentialService.getPort(),
                        credentialService.getDbname()));
        connectionPool.setUsername(credentialService.getUsername());
        connectionPool.setPassword(credentialService.getPassword());
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