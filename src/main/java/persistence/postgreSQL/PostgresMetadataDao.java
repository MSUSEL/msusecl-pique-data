package persistence.postgreSQL;

import businessObjects.cve.NvdMirrorMetaData;
import exceptions.DataAccessException;
import persistence.IDao;
import persistence.IDataSource;

import java.sql.*;
import java.util.Collections;
import java.util.List;

import static persistence.postgreSQL.StoredProcedureCalls.UPSERT_METADATA;

public final class PostgresMetadataDao {
    private final Connection conn;

    public PostgresMetadataDao(IDataSource<Connection> dataSource) {
        this.conn = dataSource.getConnection();
    }

    public List<NvdMirrorMetaData> fetch() throws DataAccessException {
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

            return Collections.singletonList(metaData);

        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public void upsert(List<NvdMirrorMetaData> metadata) throws DataAccessException {
        insertMetadata(metadata.get(0));
    }


    private void insertMetadata(NvdMirrorMetaData metadata) {
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
