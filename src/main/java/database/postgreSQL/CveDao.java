package database.postgreSQL;

import java.sql.Connection;

import api.cveData.Cve;
import common.ICredentialService;
import database.AbstractBaseDao;
import database.IDatabaseConnection;

public class CveDao extends AbstractBaseDao<Cve, Connection> {

    protected final Connection dbConnection;

    public CveDao(IDatabaseConnection<Connection> dbConnection, ICredentialService credentialService) {
        this.dbConnection = dbConnection.getConnection(
                credentialService.getDriver(),
                credentialService.getHostname(),
                credentialService.getPort(),
                credentialService.getDbname(),
                credentialService.getUsername(),
                credentialService.getPassword());
    }

    @Override
    public Cve getById(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getById'");
    }

    @Override
    public void insert(Cve cve) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insert'");
    }

    @Override
    public void update(Cve cve) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void delete(Cve cve) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }
    
}