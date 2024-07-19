package persistence.postgreSQL;

import java.sql.*;

import common.Constants;
import exceptions.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import businessObjects.cve.Cve;
import handlers.IJsonMarshaller;
import persistence.IDao;
import persistence.IDataSource;

public final class PostgresCveDao implements IDao<Cve> {
    private final Connection conn;
    private final IJsonMarshaller<Cve> cveDetailsMarshaller;
    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresCveDao.class);

    public PostgresCveDao(IDataSource<Connection> dataSource, IJsonMarshaller<Cve> cveMarshaller) {
        this.cveDetailsMarshaller = cveMarshaller;
        this.conn = dataSource.getConnection();
    }

    @Override
    public Cve fetchById(String id) throws DataAccessException {
        try {
            String sql = "SELECT details FROM nvd.cve WHERE cve_id = ?;";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, id);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String result = rs.getString("details");
                return cveDetailsMarshaller.unmarshalJson(result);
            } else {
                LOGGER.info(Constants.DB_QUERY_NO_RESULTS);
                throw new DataAccessException(Constants.DB_QUERY_NO_RESULTS);
            }
        } catch (SQLException e) {
            LOGGER.error(Constants.DB_QUERY_FAILED, e);
            throw new DataAccessException(Constants.DB_QUERY_FAILED, e);
        }
    }

    @Override
    public void insert(Cve cve) throws DataAccessException {
        String sql = "INSERT INTO nvd.cve (cve_id, details) VALUES (?, CAST(? AS jsonb));";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, cve.getId());
            statement.setString(2, cveDetailsMarshaller.marshalJson(cve));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(Constants.DB_QUERY_FAILED, e);
        }
    }

    @Override
    public void update(Cve cve) throws DataAccessException {
        try {
            CallableStatement callableStatement = conn.prepareCall("{call update_cve_details(?, ?)}");
            callableStatement.setString(1, cve.getId());
            callableStatement.setString(2, cveDetailsMarshaller.marshalJson(cve));
            callableStatement.execute();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public void delete(String cveId) throws DataAccessException {
        String sql = "DELETE FROM nvd.cve WHERE cve_id = ? RETURNING cve_id;";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, cveId);
            statement.executeQuery();
        } catch (SQLException e) {
            throw new DataAccessException("Query failed. ", e);
        }
    }
}