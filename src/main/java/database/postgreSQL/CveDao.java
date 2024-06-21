package database.postgreSQL;

import java.sql.Connection;
import java.sql.SQLException;

import api.cveData.Cve;
import database.IDao;

public class CveDao implements IDao {

    private final Connection dbConnection;

    public CveDao() throws SQLException {
        dbConnection = PostgresConnectionManager.getConnection();
    }

    @Override
    public Cve getById(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getById'");
    }

    @Override
    public void insert(Object t) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insert'");
    }

    @Override
    public void update(Object t) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void delete(Object t) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

}