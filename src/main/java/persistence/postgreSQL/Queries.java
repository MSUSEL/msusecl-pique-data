package persistence.postgreSQL;

import lombok.Getter;

@Getter
public class Queries {
    
    private String getCveById = "SELECT details " +
    "FROM nvd_mirror.cve " +
    "WHERE cve_id LIKE '?'";
}
