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
            "\"vulnerability\" jsonb, " +
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
