package persistence.postgreSQL;

import businessObjects.cve.Cve;
import common.Constants;
import exceptions.ApiCallException;
import exceptions.DataAccessException;
import handlers.CveMarshaller;
import handlers.IJsonMarshaller;
import persistence.IBulkDao;
import persistence.IDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public final class PostgresBulkCveDao implements IBulkDao<Cve> {
    private final Connection conn;
    private final IJsonMarshaller<Cve> marshaller;

    public PostgresBulkCveDao(IDataSource<Connection> dataSource, IJsonMarshaller<Cve> marshaller) {
        this.conn = dataSource.getConnection();
        this.marshaller = marshaller;
    }

    @Override
    public void insertMany(List<Cve> entity) throws DataAccessException {
        // TODO create a stored procedure to improve performance and do this in bulk
        for (Cve cve : entity) {
            String sql = "INSERT INTO nvd.cve (cve_id, details) VALUES (?, CAST(? AS jsonb));";
            try {
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setString(1, cve.getId());
                statement.setString(2, marshaller.marshalJson(cve));
                statement.executeUpdate();
            } catch(SQLException e) {
                throw new DataAccessException("Insert statement failed", e);
            }
        }

    }

    @Override
    public Cve[] fetchMany(String[] entities) {
        return new Cve[0];
    }

    @Override
    public Cve[] fetchAll() {
        return new Cve[0];
    }
}
