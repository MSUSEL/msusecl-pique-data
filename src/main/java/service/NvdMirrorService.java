package service;

import businessObjects.cveData.Cve;
import businessObjects.cveData.NvdMirrorMetaData;
import database.IBulkDao;
import database.IDao;
import database.IMetaDataDao;

public class NvdMirrorService {
    private final CveResponseProcessor cveResponseProcessor = new CveResponseProcessor();
    private final DbContextResolver dbContextResolver = new DbContextResolver();

    public Cve handleGetCveById(String dbContext, String cveId) {
        IDao<Cve> dao = dbContextResolver.resolveCveDao(dbContext);
        return dao.fetchById(cveId);
    }

    public Cve[] handleGetCveById(String dbContext, String[] cveIds) {
        IBulkDao<Cve> dao = dbContextResolver.resolveBulkDao(dbContext);
        return dao.fetchMany(cveIds);
    }

    public String[] handleGetCwes(String dbContext, String cveId) {
        Cve cve = handleGetCveById(dbContext, cveId);
        return cveResponseProcessor.extractCwes(cve);
    }

    public NvdMirrorMetaData handleGetCurrentMetaData(String dbContext) {
        IMetaDataDao<NvdMirrorMetaData> dao = dbContextResolver.resolveMetaDataDao(dbContext);
        return dao.fetchMetaData();
    }
}
