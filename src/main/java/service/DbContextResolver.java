package service;

import businessObjects.cve.Cve;
import businessObjects.cve.NvdMirrorMetaData;
import common.Constants;
import handlers.CveMarshaller;
import persistence.IBulkDao;
import persistence.IDao;
import persistence.IMetaDataDao;
import persistence.mongo.MongoBulkCveDao;
import persistence.mongo.MongoConnectionManager;
import persistence.mongo.MongoCveDao;
import persistence.mongo.MongoMetaDataDao;
import persistence.postgreSQL.PostgresBulkCveDao;
import persistence.postgreSQL.PostgresConnectionManager;
import persistence.postgreSQL.PostgresCveDao;
import persistence.postgreSQL.PostgresMetaDataDao;

public final class DbContextResolver {
    public IBulkDao<Cve> resolveBulkDao(String dbContext) {
        return dbContext.equals(Constants.DB_CONTEXT_LOCAL)
                ? new MongoBulkCveDao(new MongoConnectionManager(), new CveMarshaller())
                : new PostgresBulkCveDao(new PostgresConnectionManager(), new CveMarshaller());
    }

    public IMetaDataDao<NvdMirrorMetaData> resolveMetaDataDao(String dbContext) {
        return dbContext.equals(Constants.DB_CONTEXT_LOCAL)
                ? new MongoMetaDataDao(new MongoConnectionManager())
                : new PostgresMetaDataDao(new PostgresConnectionManager());
    }

    public IDao<Cve> resolveCveDao(String dbContext) {
        return dbContext.equals(Constants.DB_CONTEXT_LOCAL)
                ? new MongoCveDao(new MongoConnectionManager(), new CveMarshaller())
                : new PostgresCveDao(new PostgresConnectionManager(), new CveMarshaller());
    }
}
