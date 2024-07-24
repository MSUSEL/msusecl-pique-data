package persistence.postgreSQL;

import businessObjects.cve.Cve;
import exceptions.DataAccessException;
import handlers.IJsonMarshaller;
import persistence.IBulkDao;
import persistence.IDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
    public List<Cve> fetchMany(String[] entities) throws DataAccessException {
        String base = "SELECT details FROM nvd.cve WHERE cve_id IN (";
        StringBuilder idListBuilder = new StringBuilder();
        try {
            for (int i = 0; i < entities.length; i++) {
                idListBuilder.append("'").append(entities[i]).append("'");
                if (i < entities.length - 1) {
                    idListBuilder.append(", ");
                }
            }
            idListBuilder.append(");");
            String idList = idListBuilder.toString();
            String sql = base + idList;
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();

            List<Cve> result = new ArrayList<>();
            while (rs.next()) {
                result.add(marshaller.unmarshalJson(rs.getString("details")));
            }

            return result;

        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public Cve[] fetchAll() {
        return new Cve[0];
    }
}
