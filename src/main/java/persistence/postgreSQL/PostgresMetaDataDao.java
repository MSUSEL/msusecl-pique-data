package persistence.postgreSQL;

import exceptions.DataAccessException;
import persistence.IDataSource;
import persistence.IMetaDataDao;
import businessObjects.cve.NvdMirrorMetaData;

import java.sql.*;

public final class PostgresMetaDataDao implements IMetaDataDao<NvdMirrorMetaData> {
    private final Connection conn;

    public PostgresMetaDataDao(IDataSource<Connection> dataSource) {
        this.conn = dataSource.getConnection();
    }

    @Override
    public boolean updateMetaData(NvdMirrorMetaData metaData) throws DataAccessException {
        try {
            String sql = String.format("UPDATE nvd.cve " +
                            "SET totalResults = %s, " +
                            "format = %s, " +
                            "version = %s, " +
                            "timestamp = %s;",
                    metaData.getTotalResults(),
                    metaData.getFormat(),
                    metaData.getVersion(),
                    metaData.getTimestamp());

            PreparedStatement statement = conn.prepareStatement(sql);
            return statement.execute();
        }
        catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public NvdMirrorMetaData fetchMetaData() throws DataAccessException {
        try {
            String sql = "SELECT * FROM nvd.metadata;";
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            NvdMirrorMetaData metaData = new NvdMirrorMetaData();

            if (rs.next()) {
                String id = rs.getString("id");
                String totalResults = rs.getString("totalResults");
                String format = rs.getString("format");
                String version = rs.getString("version");
                String timestamp = rs.getString("timestamp");

                metaData.setId(id);
                metaData.setTotalResults(totalResults);
                metaData.setFormat(format);
                metaData.setVersion(version);
                metaData.setTimestamp(timestamp);
            }

            return metaData;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}
