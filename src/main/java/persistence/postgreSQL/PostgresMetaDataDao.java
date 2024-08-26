package persistence.postgreSQL;

import common.Constants;
import exceptions.DataAccessException;
import org.apache.commons.lang3.NotImplementedException;
import persistence.IDao;
import persistence.IDataSource;
import businessObjects.cve.NvdMirrorMetaData;

import java.sql.*;
import java.util.Collections;
import java.util.List;

public final class PostgresMetaDataDao implements IDao<NvdMirrorMetaData> {
    private final Connection conn;

    public PostgresMetaDataDao(IDataSource<Connection> dataSource) {
        this.conn = dataSource.getConnection();
    }

    @Override
    public void update(List<NvdMirrorMetaData> metaData) throws DataAccessException {
        try {
            String sql = String.format("UPDATE nvd.metadata " +
                            "SET total_results = '%s', " +
                            "format = '%s', " +
                            "api_version = '%s', " +
                            "last_timestamp = '%s'::text;",
                    metaData.get(0).getTotalResults(),
                    metaData.get(0).getFormat(),
                    metaData.get(0).getVersion(),
                    metaData.get(0).getTimestamp());

            PreparedStatement statement = conn.prepareStatement(sql);
            statement.execute();
        }
        catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public void delete(List<String> t) throws DataAccessException {
        throw new NotImplementedException(Constants.METHOD_NOT_IMPLEMENTED_MESSAGE);
    }

    @Override
    public void dumpToFile(String filePath) throws DataAccessException {

    }

    public List<NvdMirrorMetaData> fetch(List<String> metadataId) throws DataAccessException {
        try {
            String sql = "SELECT * FROM nvd.metadata WHERE id LIKE ?;";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, metadataId.get(0));
            ResultSet rs = statement.executeQuery();
            NvdMirrorMetaData metaData = new NvdMirrorMetaData();

            if (rs.next()) {
                String id = rs.getString("id");
                String totalResults = rs.getString("total_results");
                String format = rs.getString("format");
                String version = rs.getString("api_version");
                String timestamp = rs.getString("last_timestamp");

                metaData.setId(id);
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
    public void insert(List<NvdMirrorMetaData> t) throws DataAccessException {
        throw new NotImplementedException(Constants.METHOD_NOT_IMPLEMENTED_MESSAGE);
    }
}
