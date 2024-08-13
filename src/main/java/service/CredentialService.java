package service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CredentialService {
    private String dbContext = System.getenv("DB_CONTEXT");
    private String driver = System.getenv("PG_DRIVER");
    private String hostname = System.getenv("PG_HOSTNAME");
    private String port = System.getenv("PG_PORT");
    private String dbname = System.getenv("PG_DBNAME");
    private String username = System.getenv("PG_USERNAME");
    private String password = System.getenv("PG_PASS");
}
