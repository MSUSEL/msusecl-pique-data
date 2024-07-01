package presentation;

import businessObjects.cve.Cve;
import businessObjects.ghsa.SecurityAdvisory;
import exceptions.ApiCallException;
import exceptions.DataAccessException;
import service.GhsaApiService;
import service.NvdApiService;
import service.NvdMirrorService;

public class PiqueData {
    private static final NvdMirrorService nvdMirrorService = new NvdMirrorService();
    private static final NvdApiService nvdApiService = new NvdApiService();
    private static final GhsaApiService ghsaApiService = new GhsaApiService();

    // ---------------------------------------------------------------------
    //     Methods to interact with CVEs
    // ---------------------------------------------------------------------
    public static Cve getCveById(String dbContext, String cveId) throws DataAccessException {
        return nvdMirrorService.handleGetCveById(dbContext, cveId);
    }

    public static Cve[] getCveById(String dbContext, String[] cveIds) throws DataAccessException {
        return nvdMirrorService.handleGetCveById(dbContext, cveIds);
    }

    public static String[] getCwes(String dbContext, String cveId) throws DataAccessException{
        return nvdMirrorService.handleGetCwes(dbContext, cveId);
    }

    public static Cve getCveFromNvd(String cveId) throws ApiCallException {
        return nvdApiService.handleGetCveFromNvd(cveId);
    }

    // ---------------------------------------------------------------------
    //     Methods to interact with GHSAs
    // ---------------------------------------------------------------------
    public static SecurityAdvisory getGhsa(String ghsaId) throws ApiCallException {
        return ghsaApiService.handleGetGhsa(ghsaId);
    }

}
