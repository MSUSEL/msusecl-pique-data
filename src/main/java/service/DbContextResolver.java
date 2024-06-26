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

import java.util.List;

public class DbContextResolver {

    public IBulkDao<List<Cve>> getBulkDao(String dbContext) {
        return dbContext.equals(Utils.DB_CONTEXT_LOCAL) ? new MongoBulkCveDao() : new PostgresBulkCveDao();
    }

    public IMetaDataDao<NvdMirrorMetaData> getMetaDataDao(String dbContext) {
        return dbContext.equals(Utils.DB_CONTEXT_LOCAL) ? new MongoMetaDataDao() : new PostgresMetaDataDao();
    }

    public IDao<Cve> getCveDao(String dbContext) {
        return dbContext.equals(Utils.DB_CONTEXT_LOCAL) ? new MongoCveDao() : new PostgresCveDao();
    }
}
