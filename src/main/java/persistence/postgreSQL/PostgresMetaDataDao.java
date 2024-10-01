package persistence.postgreSQL;

import businessObjects.cve.NvdMirrorMetaData;
import exceptions.DataAccessException;
import persistence.IDao;
import persistence.IDataSource;

import java.sql.*;
import java.util.Collections;
import java.util.List;

import static persistence.postgreSQL.StoredProcedureCalls.UPSERT_METADATA;

public final class PostgresMetaDataDao implements IDao<NvdMirrorMetaData> {
    private final Connection conn;

    public PostgresMetaDataDao(IDataSource<Connection> dataSource) {
        this.conn = dataSource.getConnection();
    }

    public List<NvdMirrorMetaData> fetch(List<String> metadataId) throws DataAccessException {
        try {
            String sql = "SELECT * FROM nvd.metadata WHERE id = ?;";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(metadataId.get(0)));
            ResultSet rs = statement.executeQuery();
            NvdMirrorMetaData metaData = new NvdMirrorMetaData();

            if (rs.next()) {
                String totalResults = rs.getString("total_results");
                String format = rs.getString("format");
                String version = rs.getString("api_version");
                String timestamp = rs.getString("last_timestamp");

                metaData.setTotalResults(totalResults);
                metaData.setFormat(format);
                metaData.setVersion(version);
                metaData.setTimestamp(timestamp);
            }

            return Collections.singletonList(metaData);

        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public void upsert(List<NvdMirrorMetaData> metadata) throws DataAccessException {
        insertMetadata(metadata.get(0));
    }

    @Override
    public void delete(List<String> ids) throws DataAccessException {

    }

    private void insertMetadata(NvdMirrorMetaData metadata) {
        try {
            CallableStatement statement = conn.prepareCall(UPSERT_METADATA);
            statement.setInt(1, Integer.parseInt(metadata.getTotalResults()));
            statement.setString(2, metadata.getFormat());
            statement.setString(3, metadata.getVersion());
            statement.setString(4, metadata.getTimestamp());
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
