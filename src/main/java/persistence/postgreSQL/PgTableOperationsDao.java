package persistence.postgreSQL;

public class PgTableOperationsDao {

    public static final String dropCveData = "DROP TABLE IF EXISTS \"cve\";\n" +
            "DROP SEQUENCE IF EXISTS cve_id_seq;\n" +
            "CREATE SEQUENCE cve_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1;\n";

    public static final String createCveTable = "CREATE TABLE \"nvd_mirror\".\"cve\" ( " +
            "\"id\" integer DEFAULT nextval('cve_id_seq') NOT NULL, " +
            "\"cve_id\" text, " +
            "\"details\" jsonb, " +
            "CONSTRAINT \"cve_cve_id\" UNIQUE (\"cve_id\"), " +
            "CONSTRAINT \"cve_pkey\" PRIMARY KEY (\"id\") " +
            ") WITH (oids = false); ";
}
