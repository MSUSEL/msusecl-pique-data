package common;

import java.io.IOException;
import java.util.Properties;

public class PostgresCredentialService implements ICredentialService {
    private String propPath = "src/main/resources/postgres.properties";
    private Properties pgProps; 
    
    public PostgresCredentialService() throws IOException {
        this.pgProps = DataProperties.getProperties(propPath);
    }

    @Override
    public String getUsername() {
        return pgProps.getProperty("username");
    }

    @Override
    public String getPassword() {
        return pgProps.getProperty("password");
    }

    @Override
    public String getDriver() {
        return pgProps.getProperty("driver");
    }

    @Override
    public String getHostname() {
        return pgProps.getProperty("hostname");
    }

    @Override
    public String getPort() {
        return pgProps.getProperty("port");
    }

    @Override
    public String getDbname() {
        return pgProps.getProperty("dbname");
    }
}
