package service;

import api.cveData.Cve;
import common.Utils;
import database.IDao;
import database.mongo.MongoCveDao;
import database.postgreSQL.PostgresCveDao;

public class NvdMirrorService {
    private final CveResponseProcessor cveResponseProcessor = new CveResponseProcessor();

    public Cve handleGetCveById(String dbContext, String cveId) {
        IDao<Cve> dao = resolveDbContext(dbContext);
        return dao.getById(cveId);
    }

    public String[] handleGetCwes(String dbContext, String cveId) {
        Cve cve = handleGetCveById(dbContext, cveId);
        return cveResponseProcessor.extractCwes(cve);
    }

    private IDao<Cve> resolveDbContext(String dbContext) {
        return dbContext.equals(Utils.DB_CONTEXT_LOCAL) ? new MongoCveDao() : new PostgresCveDao();
    }
}
