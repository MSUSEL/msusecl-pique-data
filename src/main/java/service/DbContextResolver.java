package service;

import businessObjects.cveData.Cve;
import businessObjects.cveData.NvdMirrorMetaData;
import common.Utils;
import database.IBulkDao;
import database.IDao;
import database.IMetaDataDao;
import database.mongo.MongoBulkCveDao;
import database.mongo.MongoCveDao;
import database.mongo.MongoMetaDataDao;
import database.postgreSQL.PostgresBulkCveDao;
import database.postgreSQL.PostgresCveDao;
import database.postgreSQL.PostgresMetaDataDao;

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
