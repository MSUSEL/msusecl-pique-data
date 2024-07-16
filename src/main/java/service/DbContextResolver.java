package service;

import businessObjects.cve.Cve;
import businessObjects.cve.NvdMirrorMetaData;
import common.Constants;
import common.DataUtilityProperties;
import handlers.CveMarshaller;
import handlers.MetaDataMarshaller;
import persistence.IBulkDao;
import persistence.IDao;
import persistence.IDataSource;
import persistence.IMetaDataDao;
import persistence.mongo.MongoBulkCveDao;
import persistence.mongo.MongoConnectionManager;
import persistence.mongo.MongoCveDao;
import persistence.mongo.MongoMetaDataDao;
import persistence.postgreSQL.PostgresBulkCveDao;
import persistence.postgreSQL.PostgresConnectionManager;
import persistence.postgreSQL.PostgresCveDao;
import persistence.postgreSQL.PostgresMetaDataDao;

import java.util.Properties;

public final class DbContextResolver {
    public IBulkDao<Cve> resolveBulkDao(String dbContext) {
        return dbContext.equals(Constants.DB_CONTEXT_LOCAL)
                ? new MongoBulkCveDao(new MongoConnectionManager(), new CveMarshaller())
                : new PostgresBulkCveDao(new PostgresConnectionManager(), new CveMarshaller());
    }

    public IMetaDataDao<NvdMirrorMetaData> resolveMetaDataDao(String dbContext) {
        return dbContext.equals(Constants.DB_CONTEXT_LOCAL)
                ? new MongoMetaDataDao(new MongoConnectionManager(), new MetaDataMarshaller())
                : new PostgresMetaDataDao(new PostgresConnectionManager(), new MetaDataMarshaller());
    }

    public IDao<Cve> resolveCveDao(String dbContext) {
        return dbContext.equals(Constants.DB_CONTEXT_LOCAL)
                ? new MongoCveDao(new MongoConnectionManager(), new CveMarshaller())
                : new PostgresCveDao(new PostgresConnectionManager(), new CveMarshaller());
    }
}
