package persistence.postgreSQL;

import lombok.Getter;

@Getter
public class Queries {
    
    private final String getCveById = "SELECT details " +
    "FROM nvd_mirror.cve " +
    "WHERE cve_id LIKE '?'";
}
