package service;

import businessObjects.cve.Cve;
import businessObjects.cve.Metrics;
import businessObjects.cve.NvdMirrorMetaData;
import persistence.IDao;
import exceptions.DataAccessException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
    public List<String> handleGetNvdCweName(String cveId) throws DataAccessException {
        return cveResponseProcessor.extractCweDescriptions(handleGetCveById(cveId));
    }

    // FIXME: fetch metadata by something other than auto-generated id number
    @Override
    public NvdMirrorMetaData handleGetCurrentMetaData() throws DataAccessException {
        return metadataDao.fetch(Collections.singletonList("1")).get(0);
    }

    @Override
    public void handleInsertSingleCve(Cve cve) throws DataAccessException {
        cveDao.upsert(Collections.singletonList(cve));
    }

    @Override
    public void handleDeleteSingleCve(String cveId) throws DataAccessException {
        cveDao.delete(Collections.singletonList(cveId));
    }

    @Override
    public Map<String, Metrics> handleGetCvssMetrics(List<String> cveIds) throws DataAccessException {
        List<Cve> cves = handleGetCveById(cveIds);
        return cveResponseProcessor.extractCvssScores(cves);
    }
}
