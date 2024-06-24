package database.postgreSQL;

public class Queries {
    
    private String cveTableInit = "CREATE TABLE if NOT EXISTS nvd_cve ( " +
    "id SERIAL PRIMARY KEY, " +
    "cve jsonb " +
    ")";
}
