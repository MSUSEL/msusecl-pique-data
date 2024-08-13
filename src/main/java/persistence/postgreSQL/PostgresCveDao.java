package persistence.postgreSQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import common.Constants;
import exceptions.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import businessObjects.cve.Cve;
import handlers.IJsonMarshaller;
import persistence.IDao;
import persistence.IDataSource;


// TODO move functionality to stored procedures
public final class PostgresCveDao implements IDao<Cve> {
    private final Connection conn;
    private final IJsonMarshaller<Cve> cveDetailsMarshaller;
    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresCveDao.class);

    public PostgresCveDao(IDataSource<Connection> dataSource, IJsonMarshaller<Cve> cveMarshaller) {
        this.cveDetailsMarshaller = cveMarshaller;
        this.conn = dataSource.getConnection();
    }

    @Override
    public List<Cve> fetch(List<String> ids) throws DataAccessException {
        List<Cve> result;

        if(ids.size() > 1) {
            result = performBulkFetch(ids);
        } else {
            result = performFetch(ids.get(0));
        }

        return result;
    }

    @Override
    public void insert(List<Cve> cves) throws DataAccessException {
        if (cves.size() > 1) {
            performBulkInsert(cves);
        } else {
            performInsert(cves);
        }
    }

    @Override
    public void update(List<Cve> cves) throws DataAccessException {
        if (cves.size() > 1) {
            performBulkUpdate(cves);
        } else {
            performUpdate(cves);
        }
    }

    @Override
    public void delete(List<String> cveIds) throws DataAccessException {

        if (cveIds.size() > 1) {
            performDelete(cveIds);
        } else {
            performBulkDelete(cveIds);
        }
    }

    private void performInsert(List<Cve> cves) throws DataAccessException {
        String sql = "INSERT INTO nvd.cve (cve_id, details) VALUES (?, CAST(? AS jsonb));";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, cves.get(0).getId());
            statement.setString(2, cveDetailsMarshaller.marshalJson(cves.get(0)));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(Constants.DB_QUERY_FAILED, e);
        }
    }

    private void performBulkInsert(List<Cve> cves) throws DataAccessException {
        for (Cve cve : cves) {
            String sql = "INSERT INTO nvd.cve (cve_id, details) VALUES (?, CAST(? AS jsonb));";
            try {
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setString(1, cve.getId());
                statement.setString(2, cveDetailsMarshaller.marshalJson(cve));
                statement.executeUpdate();
            } catch(SQLException e) {
                throw new DataAccessException("Insert statement failed", e);
            }
        }
    }

    private List<Cve> performBulkFetch(List<String> ids) throws DataAccessException {
        String base = "SELECT details FROM nvd.cve WHERE cve_id IN (";
        String sql = formatBulkFetchSQL(ids, base);
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            List<Cve> result = new ArrayList<>();
            while (rs.next()) {
                result.add((Cve) cveDetailsMarshaller.unmarshalJson(rs.getString("details")));
            }

            return result;

        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    private List<Cve> performFetch(String id) throws DataAccessException {
        try {
            String sql = "SELECT details FROM nvd.cve WHERE cve_id = ?;";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, id);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String result = rs.getString("details");
                return Collections.singletonList((Cve) cveDetailsMarshaller.unmarshalJson(result));
            } else {
                LOGGER.info(Constants.DB_QUERY_NO_RESULTS);
                throw new DataAccessException(Constants.DB_QUERY_NO_RESULTS);
            }
        } catch (SQLException e) {
            LOGGER.error(Constants.DB_QUERY_FAILED, e);
            throw new DataAccessException(Constants.DB_QUERY_FAILED, e);
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

    private void performBulkDelete(List<String> cveIds) throws DataAccessException {
        for (String id : cveIds) {
            performDelete(cveIds);
        }
    }

    private void performDelete(List<String> cveId) throws DataAccessException {
        String sql = "DELETE FROM nvd.cve WHERE cve_id = ? RETURNING cve_id;";

        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, cveId.get(0));
            statement.executeQuery();
        } catch (SQLException e) {
            throw new DataAccessException("Query failed. ", e);
        }
    }

    private void performBulkUpdate(List<Cve> cves) throws DataAccessException {
        for (Cve cve : cves) {
            performUpdate(cves);
        }
    }

    private void performUpdate(List<Cve> cves) throws DataAccessException {
        try {
            CallableStatement callableStatement = conn.prepareCall("{call update_cve_details(?, ?)}");
            callableStatement.setString(1, cves.get(0).getId());
            callableStatement.setString(2, cveDetailsMarshaller.marshalJson(cves.get(0)));
            callableStatement.execute();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}