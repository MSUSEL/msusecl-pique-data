package presentation;

import businessObjects.cveData.Cve;
import service.NvdApiService;
import service.NvdMirrorService;

public class PiqueData {
    private static final NvdMirrorService nvdMirrorService = new NvdMirrorService();
    private static final NvdApiService nvdAPIService = new NvdApiService();

    public static Cve getCveById(String dbContext, String cveId) {
        return nvdMirrorService.handleGetCveById(dbContext, cveId);
    }

    public static Cve[] getCveById(String dbContext, String[] cveIds) {
        return nvdMirrorService.handleGetCveById(dbContext, cveIds);
    }

    public static String[] getCwes(String dbContext, String cveId) {
        return nvdMirrorService.handleGetCwes(dbContext, cveId);
    }

    public static Cve getCveFromNvd(String cveId) {
        return nvdAPIService.handleGetCveFromNvd(cveId);
    }

}
