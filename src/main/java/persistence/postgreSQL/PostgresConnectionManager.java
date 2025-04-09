/*
 * MIT License
 *
 * Copyright (c) 2024 Montana State University Software Engineering and Cybersecurity Laboratory
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package persistence.postgreSQL;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import org.apache.commons.dbcp2.BasicDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.IDataSource;
import service.CredentialService;


public final class PostgresConnectionManager implements IDataSource<Connection> {
    private final BasicDataSource connectionPool = new BasicDataSource();
    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresConnectionManager.class);

    public PostgresConnectionManager(CredentialService credentialService) {
        setConnectionPoolUrl(credentialService);
        Optional<String> username = credentialService.getUsername();
        Optional<String> password = credentialService.getPassword();
        // For peer authentication to postgres, username and password will be inferred from the OS.
        // This is a rare use case and really only applies to client programs running locally
        // on our CI server. When peer authentication is not used, explicitly set username and password.
        if (credentialService.getUsername().isPresent() && credentialService.getPassword().isPresent()) {
            connectionPool.setUsername(username.get());
            connectionPool.setPassword(password.get());
        }
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

    private void setConnectionPoolUrl(CredentialService credentialService) {
        connectionPool.setUrl(
                buildConnectionString(
                        credentialService.getDriver(),
                        credentialService.getHostname(),
                        credentialService.getPort(),
                        credentialService.getDbname()));
    }
}