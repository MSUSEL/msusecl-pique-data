package service;

import businessObjects.cveData.Cve;
import businessObjects.cveData.NvdMirrorMetaData;
import common.Utils;
import persistence.IBulkDao;
import persistence.IDao;
import persistence.IMetaDataDao;
import persistence.mongo.MongoBulkCveDao;
import persistence.mongo.MongoCveDao;
import persistence.mongo.MongoMetaDataDao;
import persistence.postgreSQL.PostgresBulkCveDao;
import persistence.postgreSQL.PostgresCveDao;
import persistence.postgreSQL.PostgresMetaDataDao;

public class DbContextResolver {

    public IBulkDao<Cve> resolveBulkDao(String dbContext) {
        return dbContext.equals(Utils.DB_CONTEXT_LOCAL) ? new MongoBulkCveDao() : new PostgresBulkCveDao();
    }

    public IMetaDataDao<NvdMirrorMetaData> resolveMetaDataDao(String dbContext) {
        return dbContext.equals(Utils.DB_CONTEXT_LOCAL) ? new MongoMetaDataDao() : new PostgresMetaDataDao();
    }

    public IDao<Cve> resolveCveDao(String dbContext) {
        return dbContext.equals(Utils.DB_CONTEXT_LOCAL) ? new MongoCveDao() : new PostgresCveDao();
    }
}
