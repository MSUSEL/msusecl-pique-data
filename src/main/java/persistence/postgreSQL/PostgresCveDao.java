package persistence.postgreSQL;

import businessObjects.cve.Cve;
import exceptions.DataAccessException;
import handlers.IJsonMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.IDao;
import persistence.IDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static common.Constants.DB_QUERY_FAILED;
import static common.Constants.DB_QUERY_NO_RESULTS;
import static persistence.postgreSQL.StoredProcedureCalls.*;

public final class PostgresCveDao implements IDao<Cve> {
    private final Connection conn;
    private final IJsonMarshaller<Cve> cveDetailsMarshaller;
    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresCveDao.class);

    public PostgresCveDao(IDataSource<Connection> dataSource, IJsonMarshaller<Cve> cveMarshaller) {
        this.cveDetailsMarshaller = cveMarshaller;
        this.conn = dataSource.getConnection();
    }

    private static class CveInsertParams {
        static String[] cveIds;
        static Object[] details;
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
    public void upsert(List<Cve> cves) throws DataAccessException {
        if (cves.size() > 1) {
            performBulkInsert(cves);
        } else {
            performUpsert(cves.get(0));
        }
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

    private void performUpsert(Cve cve) throws DataAccessException {
        try {
            CallableStatement statement = conn.prepareCall(UPSERT_CVE);
            statement.setString(1, cve.getId());
            statement.setString(2, cveDetailsMarshaller.marshalJson(cve));
            statement.execute();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    private void performBulkInsert(List<Cve> cves) throws DataAccessException {
        formatCveInsertParams(cves);
        executePGBulkInsertCall();
    }

    private void formatCveInsertParams(List<Cve> cves) {
        int size = cves.size();
        String[] cve_ids = new String[size];
        String[] stringDetails = new String[size];

        for (int i = 0; i < cves.size(); i++) {
            cve_ids[i] = cves.get(i).getId();
            stringDetails[i] = cveDetailsMarshaller.marshalJson(cves.get(i));
        }
        Object[] jsonbArray = formatJsonbArray(stringDetails);

        CveInsertParams.cveIds = cve_ids;
        CveInsertParams.details = jsonbArray;
    }

    private void executePGBulkInsertCall() throws DataAccessException {
        try {
            CallableStatement statement = conn.prepareCall(UPSERT_BULK_CVES);
            statement.setArray(1, conn.createArrayOf("TEXT", CveInsertParams.cveIds));
            statement.setArray(2, conn.createArrayOf("JSONB", CveInsertParams.details));
            statement.execute();
        } catch (SQLException e) {
            throw new DataAccessException(e);
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
                result.add(cveDetailsMarshaller.unmarshalJson(rs.getString("details")));
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
                LOGGER.info(DB_QUERY_NO_RESULTS);
                throw new DataAccessException(DB_QUERY_NO_RESULTS);
            }
        } catch (SQLException e) {
            LOGGER.error(DB_QUERY_FAILED, e);
            throw new DataAccessException(DB_QUERY_FAILED, e);
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

    private void performUpdate(Cve cves) throws DataAccessException {
        try {
            CallableStatement callableStatement = conn.prepareCall("CALL update_cve_details(?, ?)");
            callableStatement.setString(1, cves.getId());
            callableStatement.setString(2, cveDetailsMarshaller.marshalJson(cves));
            callableStatement.execute();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    private Object[] formatJsonbArray(String[] details) {
        Object[] jsonbArray = new Object[details.length];
        System.arraycopy(details, 0, jsonbArray, 0, details.length);

        return jsonbArray;
    }
}