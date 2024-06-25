package presentation;

import api.cveData.Cve;
import database.IDao;
import service.NvdApiService;
import service.NvdMirrorService;


public class PiqueData {
    private static final NvdMirrorService nvdMirrorService = new NvdMirrorService();
    private static final NvdApiService nvdAPIService = new NvdApiService();

    public static Cve getCveById(String dbContext, String cveId) {
        return nvdMirrorService.handleGetCveById(dbContext, cveId);
    }

    public static String[] getCwes(String dbContext, String cveId) {
        return nvdMirrorService.handleGetCwes(dbContext, cveId);
    }

    public static Cve getCveFromNvd(String cveId) {
        return nvdAPIService.handleGetCveFromNvd(cveId);
    }

}
