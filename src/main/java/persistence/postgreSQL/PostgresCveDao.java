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

import businessObjects.cve.Cve;
import businessObjects.cve.Vulnerability;
import exceptions.DataAccessException;
import handlers.IJsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.IDao;
import persistence.IDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static persistence.postgreSQL.StoredProcedureCalls.*;

public final class PostgresCveDao implements IDao<Cve> {
    private final Connection conn;
    private final IJsonSerializer jsonSerializer;

    public PostgresCveDao(IDataSource<Connection> dataSource, IJsonSerializer jsonSerializer) {
        this.jsonSerializer = jsonSerializer;
        this.conn = dataSource.getConnection();
    }

    private static class CveInsertParams {
        static String[] cveIds;
        static Object[] cve;
    }

    @Override
    public List<Cve> fetch(List<String> ids) throws DataAccessException {
        String base = "SELECT vulnerability FROM nvd.cve WHERE cve_id IN (";
        String sql = formatBulkFetchSQL(ids, base);
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            List<Cve> result = new ArrayList<>();
            while (rs.next()) {
                result.add(jsonSerializer.deserialize(rs.getString("vulnerability"), Vulnerability.class).getCve());
            }

            return result;

        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public void upsert(List<Cve> cves) throws DataAccessException {
        formatCveInsertParams(cves);
        executePGBulkInsertCall();
    }

    @Override
    public void delete(List<String> ids) throws DataAccessException {
        try {
            CallableStatement statement = conn.prepareCall(DELETE_CVE);
            statement.setArray(1, conn.createArrayOf("text", ids.toArray()));
            statement.setString(2, "nvd.cve");
            statement.setString(3, "cve_id");
            statement.execute();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    private void formatCveInsertParams(List<Cve> cves) {
        int size = cves.size();
        String[] cve_ids = new String[size];
        String[] cve = new String[size];

        for (int i = 0; i < cves.size(); i++) {
            cve_ids[i] = cves.get(i).getId();
            cve[i] = jsonSerializer.serialize(cves.get(i));
        }
        Object[] jsonbFormattedCve = formatJsonbArray(cve);

        CveInsertParams.cveIds = cve_ids;
        CveInsertParams.cve = jsonbFormattedCve;
    }

    private void executePGBulkInsertCall() throws DataAccessException {
        try {
            CallableStatement statement = conn.prepareCall(UPSERT_BATCH_VULNERABILITIES);
            statement.setArray(1, conn.createArrayOf("TEXT", CveInsertParams.cveIds));
            statement.setArray(2, conn.createArrayOf("JSONB", CveInsertParams.cve));
            statement.execute();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    private String formatBulkFetchSQL(List<String> ids, String baseSQL) {
        StringBuilder idListBuilder = new StringBuilder();

        for (int i = 0; i < ids.size(); i++) {
            idListBuilder.append("'").append(ids.get(i)).append("'");
            if (i < ids.size() - 1) {
                idListBuilder.append(", ");
            }
        }
        idListBuilder.append(");");
        String idList = idListBuilder.toString();

        return baseSQL + idList;
    }

//    private void performUpdate(Cve cves) throws DataAccessException {
//        try {
//            CallableStatement callableStatement = conn.prepareCall(UPSERT_BATCH_VULNERABILITIES);
//            callableStatement.setString(1, cves.getId());
//            callableStatement.setString(2, jsonSerializer.serialize(cves));
//            callableStatement.execute();
//        } catch (SQLException e) {
//            throw new DataAccessException(e);
//        }
//    }

    private Object[] formatJsonbArray(String[] details) {
        Object[] jsonbArray = new Object[details.length];
        System.arraycopy(details, 0, jsonbArray, 0, details.length);

        return jsonbArray;
    }
}