package service;

import businessObjects.cve.Cve;
import businessObjects.cve.NvdMirrorMetaData;
import persistence.IDao;
import exceptions.DataAccessException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MirrorService implements INvdMirrorService{
    private final CveResponseProcessor cveResponseProcessor;
    private final IDao<Cve> cveDao;
    private final IDao<NvdMirrorMetaData> metadataDao;

    public MirrorService(CveResponseProcessor cveResponseProcessor, IDao<Cve> cveDao, IDao<NvdMirrorMetaData> metadataDao) {
        this.cveResponseProcessor = cveResponseProcessor;
        this.cveDao = cveDao;
        this.metadataDao = metadataDao;
    }

    @Override
    public Cve handleGetCveById(String cveId) throws DataAccessException {
        return cveDao.fetch(Collections.singletonList(cveId)).get(0);
    }

    @Override
    public List<Cve> handleGetCveById(List<String> cveIds) throws DataAccessException {
        return cveDao.fetch(cveIds);
    }

    @Override
    public ArrayList<String> handleGetNvdCweDescriptions(String cveId) throws DataAccessException {
        Cve cve = handleGetCveById(cveId);
        return cveResponseProcessor.extractCweDescriptions(cve);
    }

    @Override
    public NvdMirrorMetaData handleGetCurrentMetaData() throws DataAccessException {
        return metadataDao.fetch(Collections.singletonList("1")).get(0);
    }

    @Override
    public void handleInsertSingleCve(Cve cve) throws DataAccessException {
        cveDao.insert(Collections.singletonList(cve));
    }

    @Override
    public void handleDeleteSingleCve(String cveId) throws DataAccessException {
        cveDao.delete(Collections.singletonList(cveId));
    }
}
