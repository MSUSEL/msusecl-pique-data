package database.postgreSQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import businessObjects.cveData.Cve;
import handlers.CveDetailsMarshaller;
import handlers.IJsonMarshaller;
import database.IDao;

public class PostgresCveDao implements IDao<Cve> {

    private final Connection conn;
    private final IJsonMarshaller<Cve> cveDetailsMarshaller = new CveDetailsMarshaller();
    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresCveDao.class);

    public PostgresCveDao() {
        // Gets a connection from the Postgres Connection Pool
        conn = PostgresConnectionManager.getConnection();
    }

    @Override
    public Cve getById(String id) {
        try {
            String sql = "SELECT details FROM nvd_mirror.cve WHERE cve_id = ?;";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, id);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String result = rs.getString("details");
                return cveDetailsMarshaller.unmarshalJson(result);
            } else {
                // TODO fix error handling here
                LOGGER.info("Query returned no results.");
            }
        } catch (SQLException e) {
            LOGGER.warn("Database Query Failed. ", e);
        }
        // TODO I don't like this null.  Return empty CVE Object?  
        return null;
    }

    @Override
    public void insert(Cve cveDetails) {
        // TODO verify success?
        try {
            String sql = "INSERT INTO nvd_mirror.cve (cve_id, details) VALUES ($1, $2);";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, cveDetails.getId());
            statement.setString(2, cveDetailsMarshaller.marshalJson(cveDetails));
        } catch (SQLException e) {
            LOGGER.warn("Database Query Failed. ", e);
        }
    }

    @Override
    public void update(Cve t) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void delete(Cve t) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }
}