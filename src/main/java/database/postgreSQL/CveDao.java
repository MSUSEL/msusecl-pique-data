package database.postgreSQL;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.google.gson.Gson;

import api.cveData.Cve;
import database.IDao;

public class CveDao implements IDao<Cve> {

    private final Connection conn;

    public CveDao() throws SQLException {
        // TODO pass in database connection for better perfomance/decoupling?
        conn = PostgresConnectionManager.getConnection();
    }

    @Override
    public Cve getById(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getById'");
    }

    @Override
    public void insert(Cve cveDetails) {
        // TODO handle this excpetion better
        try {
            // String sql = "{CALL cve.insert_cve(?)}";
            // CallableStatement call = conn.prepareCall(sql);
            // String cve = new Gson().toJson(cveDetails);
            // call.setString(1, cveDetails.getId());
            //call.setString(2, cve);

            //call.execute();
            
            Statement stmt = conn.createStatement();
            stmt.execute("INSERT INTO nvd_mirror.cve");
        } catch (SQLException e) {
            throw new RuntimeException(e);
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