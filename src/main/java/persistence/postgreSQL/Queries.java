package database.postgreSQL;

import lombok.Getter;

@Getter
public class Queries {
    
    private String cveTableInit = "CREATE TABLE if NOT EXISTS nvd_cve ( " +
    "id SERIAL PRIMARY KEY, " +
    "cve jsonb " +
    ")";
    
    private String getCveById = "SELECT details " +
    "FROM nvd_mirror.cve " +
    "WHERE cve_id LIKE '?'";
}
