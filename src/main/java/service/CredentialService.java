package service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import common.HelperFunctions;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Paths;

@Getter
@Setter
public class CredentialService {
    private String dbContext;
    private String driver;
    private String hostname;
    private String port;
    private String dbname;
    private String username;
    private String password;

    public CredentialService() {
        this.dbContext = System.getenv("DB_CONTEXT");
        this.driver = System.getenv("PG_DRIVER");
        this.hostname = System.getenv("PG_HOSTNAME");
        this.port = System.getenv("PG_PORT");
        this.dbname = System.getenv("PG_DBNAME");
        this.username = System.getenv("PG_USERNAME");
        this.password = System.getenv("PG_PASS");
    }

    public CredentialService(String filepath) {
        if (filepath.contains("json")) {
            processJsonFile(filepath);
        } else {
            String message = "Please format your credential file using credentialsTemplate.json in the " +
                    "resources directory or set the appropriate environment variables on your system";
            System.out.println(message);
        }
    }

    private void processJsonFile(String filepath) {
        JsonObject creds = JsonParser.parseString(HelperFunctions.readJsonFile(Paths.get(filepath))).getAsJsonObject();

        this.dbContext = creds.get("dbContext").getAsString();
        this.driver= creds.get("driver").getAsString();
        this.hostname = creds.get("hostname").getAsString();
        this.port = creds.get("port").getAsString();
        this.dbname = creds.get("dbname").getAsString();
        this.username = creds.get("username").getAsString();
        this.password = creds.get("password").getAsString();
    }
}