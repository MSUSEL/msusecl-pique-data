package persistence.postgreSQL;

import persistence.IDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class PgTableOperationsDao {
    private final Connection conn;

    private final String dropCveData = "DROP TABLE IF EXISTS \"nvd\".\"cve\";\n" +
            "DROP SEQUENCE IF EXISTS cve_id_seq CASCADE;\n" +
            "CREATE SEQUENCE cve_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1;\n";

    private final String createCveTable = "CREATE TABLE IF NOT EXISTS \"nvd\".\"cve\" ( " +
            "\"id\" integer DEFAULT nextval('cve_id_seq') NOT NULL, " +
            "\"cve_id\" text, " +
            "\"Vulnerability\" jsonb, " +
            "CONSTRAINT \"cve_cve_id\" UNIQUE (\"cve_id\"), " +
            "CONSTRAINT \"cve_pkey\" PRIMARY KEY (\"id\") " +
            ") WITH (oids = false); ";

    private final String createMetaDataTable = "CREATE TABLE IF NOT EXISTS \"nvd\".\"metadata\" ( " +
            "\"id\" SERIAL PRIMARY KEY, " +
            "\"total_results\" VARCHAR NOT NULL, " +
            "\"format\" VARCHAR NOT NULL, " +
            "\"api_version\" VARCHAR NOT NULL, " +
            "\"last_timestamp\" VARCHAR NOT NULL " +
            ");";

    public PgTableOperationsDao(IDataSource<Connection> dataSource) {
        this.conn = dataSource.getConnection();
    }

    public void buildCveTable() {
        try {
            Statement dropTable = conn.createStatement();
            Statement createTable = conn.createStatement();

            dropTable.execute(dropCveData);
            createTable.execute(createCveTable);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void buildMetaDataTable() {
        try {
            Statement metaDataTable = conn.createStatement();
            metaDataTable.execute(createMetaDataTable);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
