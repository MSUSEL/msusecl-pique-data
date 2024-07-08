package service;

import businessObjects.cve.Cve;
import businessObjects.cve.NvdMirrorMetaData;
import common.Constants;
import persistence.IBulkDao;
import persistence.IDao;
import persistence.IMetaDataDao;
import persistence.mongo.MongoBulkCveDao;
import persistence.mongo.MongoCveDao;
import persistence.mongo.MongoMetaDataDao;
import persistence.postgreSQL.PostgresBulkCveDao;
import persistence.postgreSQL.PostgresCveDao;
import persistence.postgreSQL.PostgresMetaDataDao;

public final class DbContextResolver {

    public IBulkDao<Cve> resolveBulkDao(String dbContext) {
        return dbContext.equals(Constants.DB_CONTEXT_LOCAL) ? new MongoBulkCveDao() : new PostgresBulkCveDao();
    }

    public IMetaDataDao<NvdMirrorMetaData> resolveMetaDataDao(String dbContext) {
        return dbContext.equals(Constants.DB_CONTEXT_LOCAL) ? new MongoMetaDataDao() : new PostgresMetaDataDao();
    }

    public IDao<Cve> resolveCveDao(String dbContext) {
        return dbContext.equals(Constants.DB_CONTEXT_LOCAL) ? new MongoCveDao() : new PostgresCveDao();
    }
}
