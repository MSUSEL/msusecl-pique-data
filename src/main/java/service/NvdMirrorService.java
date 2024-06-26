package service;

import api.cveData.Cve;
import api.cveData.NvdMirrorMetaData;
import database.IDao;
import database.IMetaDataDao;

public class NvdMirrorService {
    private final CveResponseProcessor cveResponseProcessor = new CveResponseProcessor();
    private final DbContextResolver dbContextResolver = new DbContextResolver();

    public Cve handleGetCveById(String dbContext, String cveId) {
        IDao<Cve> dao = dbContextResolver.getCveDao(dbContext);
        return dao.getById(cveId);
    }

    public String[] handleGetCwes(String dbContext, String cveId) {
        Cve cve = handleGetCveById(dbContext, cveId);
        return cveResponseProcessor.extractCwes(cve);
    }

    public NvdMirrorMetaData handleGetCurrentMetaData(String dbContext) {
        IMetaDataDao<NvdMirrorMetaData> dao = dbContextResolver.getMetaDataDao(dbContext);
        return dao.retrieveMetaData();
    }
}
