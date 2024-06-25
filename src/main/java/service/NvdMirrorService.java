package service;

import api.cveData.Cve;
import api.cveData.Weakness;
import common.Utils;
import database.IDao;
import database.mongo.MongoCveDao;
import database.postgreSQL.PostgresCveDao;

import java.util.ArrayList;

public class NvdMirrorService {

    public Cve handleGetCveById(String dbContext, String cveId) {
        IDao<Cve> dao = resolveDbContext(dbContext);
        return dao.getById(cveId);
    }

    public String[] handleGetCwes(String dbContext, String cveId) {
        Cve cve = handleGetCveById(dbContext, cveId);
        ArrayList<Weakness> cweList = cve.getWeaknesses();

        int size = cweList.size();
        String[] cwes = new String[size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < cweList.get(i).getDescription().size(); j++) {
                cwes[i] = cweList.get(i).getDescription().get(j).getValue();
            }
        }

        return cwes;
    }

    private IDao<Cve> resolveDbContext(String dbContext) {
        return dbContext.equals(Utils.DB_CONTEXT_LOCAL) ? new MongoCveDao() : new PostgresCveDao();
    }
}
