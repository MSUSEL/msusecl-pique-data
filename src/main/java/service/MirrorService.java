package service;

import businessObjects.cve.Cve;
import businessObjects.cve.NvdMirrorMetaData;
import persistence.IBulkDao;
import persistence.IDao;
import persistence.IMetaDataDao;
import exceptions.DataAccessException;

import java.util.ArrayList;
import java.util.List;

public final class MirrorService implements INvdMirrorService{
    private final CveResponseProcessor cveResponseProcessor;
    private final IDao cveDao;
    private final IDao bulkCveDao;
    private final IDao metadataDao;

    public MirrorService(CveResponseProcessor cveResponseProcessor, IDao cveDao, IDao bulkCveDao, IDao metadataDao) {
        this.cveResponseProcessor = cveResponseProcessor;
        this.cveDao = cveDao;
        this.bulkCveDao = bulkCveDao;
        this.metadataDao = metadataDao;
    }

    @Override
    public Cve handleGetCveById(String cveId) throws DataAccessException {
        IDao<Cve> dao = dbContextResolver.resolveCveDao();
        return dao.fetchById(cveId);
    }

    @Override
    public List<Cve> handleGetCveById(List<String> cveIds) throws DataAccessException {
        IBulkDao<Cve> dao = dbContextResolver.resolveBulkDao();
        return dao.fetchMany(cveIds);
    }

    @Override
    public ArrayList<String> handleGetNvdCweDescriptions(String cveId) throws DataAccessException {
        Cve cve = handleGetCveById(cveId);
        return cveResponseProcessor.extractCweDescriptions(cve);
    }

    @Override
    public NvdMirrorMetaData handleGetCurrentMetaData() throws DataAccessException {
        IMetaDataDao<NvdMirrorMetaData> dao = dbContextResolver.resolveMetaDataDao();
        return dao.fetchMetaData();
    }

    @Override
    public void handleInsertSingleCve(Cve cve) throws DataAccessException {
        IDao<Cve> dao = dbContextResolver.resolveCveDao();
        dao.insert(cve);
    }

    @Override
    public void handleDeleteSingleCve(String cveId) throws DataAccessException {
        IDao<Cve> dao = dbContextResolver.resolveCveDao(dbContext);
        dao.delete(cveId);
    }
}
