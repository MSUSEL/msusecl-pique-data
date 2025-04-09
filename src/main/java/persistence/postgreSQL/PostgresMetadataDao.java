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

import businessObjects.cve.NvdMirrorMetaData;
import exceptions.DataAccessException;
import persistence.IDataSource;
import persistence.IMetaDataDao;

import java.sql.*;

import static persistence.postgreSQL.StoredProcedureCalls.UPSERT_METADATA;

public final class PostgresMetadataDao implements IMetaDataDao<NvdMirrorMetaData> {
    private final Connection conn;

    public PostgresMetadataDao(IDataSource<Connection> dataSource) {
        this.conn = dataSource.getConnection();
    }

    @Override
    public NvdMirrorMetaData fetch() throws DataAccessException {
        try {
            String sql = "SELECT * FROM nvd.metadata;";
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            NvdMirrorMetaData metaData = new NvdMirrorMetaData();

            if (rs.next()) {
                metaData.setCvesModified(Integer.toString(rs.getInt("cves_modified")));
                metaData.setFormat(rs.getString("format"));
                metaData.setApiVersion(rs.getString("api_version"));
                metaData.setLastTimestamp(rs.getString("last_timestamp"));
            }

            return metaData;

        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public void upsert(NvdMirrorMetaData metadata) throws DataAccessException {
        try {
            CallableStatement statement = conn.prepareCall(UPSERT_METADATA);
            statement.setInt(1, Integer.parseInt(metadata.getCvesModified()));
            statement.setString(2, metadata.getFormat());
            statement.setString(3, metadata.getApiVersion());
            statement.setString(4, metadata.getLastTimestamp());
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
