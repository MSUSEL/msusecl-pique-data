package service;

import businessObjects.cve.Cve;
import businessObjects.cve.NvdMirrorMetaData;
import persistence.IBulkDao;
import persistence.IDao;
import persistence.IMetaDataDao;
import exceptions.DataAccessException;
import presentation.CveResponseProcessor;

import java.util.List;

public final class MirrorService {
    private final CveResponseProcessor cveResponseProcessor = new CveResponseProcessor();
    private final DbContextResolver dbContextResolver = new DbContextResolver();

    public Cve handleGetCveById(String dbContext, String cveId) throws DataAccessException {
        IDao<Cve> dao = dbContextResolver.resolveCveDao(dbContext);
        return dao.fetchById(cveId);
    }

    public List<Cve> handleGetCveById(String dbContext, String[] cveIds) throws DataAccessException {
        IBulkDao<Cve> dao = dbContextResolver.resolveBulkDao(dbContext);
        return dao.fetchMany(cveIds);
    }

    public String[] handleGetCwes(String dbContext, String cveId) throws DataAccessException {
        Cve cve = handleGetCveById(dbContext, cveId);
        return cveResponseProcessor.extractCwes(cve);
    }

    public NvdMirrorMetaData handleGetCurrentMetaData(String dbContext) throws DataAccessException {
        IMetaDataDao<NvdMirrorMetaData> dao = dbContextResolver.resolveMetaDataDao(dbContext);
        return dao.fetchMetaData();
    }

    public void handleInsertSingleCve(String dbContext, Cve cve) throws DataAccessException {
        IDao<Cve> dao = dbContextResolver.resolveCveDao(dbContext);
        dao.insert(cve);
    }

    public void handleDeleteSingleCve(String dbContext, String cveId) throws DataAccessException {
        IDao<Cve> dao = dbContextResolver.resolveCveDao(dbContext);
        dao.delete(cveId);
    }
}
