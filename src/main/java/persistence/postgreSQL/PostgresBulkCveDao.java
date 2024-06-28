package persistence.postgreSQL;

import businessObjects.cveData.Cve;
import persistence.IBulkDao;

import java.util.List;

public class PostgresBulkCveDao implements IBulkDao<Cve> {
    @Override
    public void insertMany(List<Cve> entity) {

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
