package presentation;

import businessObjects.cve.Cve;
import businessObjects.ghsa.SecurityAdvisory;
import exceptions.ApiCallException;
import exceptions.DataAccessException;
import service.GhsaApiService;
import service.NvdApiService;
import service.MirrorService;

/**
 * PiqueData serves as the primary class for interacting with this library. It provides static methods to work
 * with common data needs in Pique wrappers. These methods are designed to work with multiple database contexts.
 * Currently, these contexts are "local" or "persistent". Both correspond to a constant in the Constants class
 * for ease of use. The SECL maintains a complete mirror of the National Vulnerability Database. It is an on-prem
 * postgresql database and instructions for setting up credentials can be found in the Readme in the persistence layer.
 * This mirror is for use by the Software Engineering and Cybersecuity Lab only. All others, please use the local context
 * or call directly to the NVD. (Both capabilities exist in this class) The local context builds a containerized instance
 * of MongoDB Community Edition and mirrors the NVD there. Again, more information can be found in the Persistence layer
 * Readme.
 *
 * This class is extensible. If these methods are insufficient for your PIQUE wrapper, feel free to create a derived
 * class in your wrapper. If you do extend this class, you will also need to extend the appropriate class in the
 * service layer. Please call the service layer, rather than data access objects from this class and any extensions.
 * Although this library is designed to be extensible, if this class does not provide for your data needs,
 * please speak with Ryan Cummings in the Software Engineering and Cybersecurity Lab. This utility should include all
 * common use cases for data access in the lab.
 */
public class PiqueData {
    protected static final MirrorService mirrorService = new MirrorService();
    protected static final NvdApiService nvdApiService = new NvdApiService();
    protected static final GhsaApiService ghsaApiService = new GhsaApiService();

    /**
     * Gets a cve from the specified database. (switching on dbContext) This does NOT call the NVD
     * @param dbContext local or persistent
     * @param cveId This is the official cveId from the NVD
     * @return Returns a Cve object corresponding to the provided cveId
     * @throws DataAccessException
     */
    public static Cve getCveById(String dbContext, String cveId) throws DataAccessException {
        return mirrorService.handleGetCveById(dbContext, cveId);
    }

    /**
     * Returns a list of Cve objects from the specified database.
     * @param dbContext local or persistent
     * @param cveIds String array of cveIds
     * @return Returns an array of CVE objects corresponding to the provided cveIds
     * @throws DataAccessException
     */
    public static Cve[] getCveById(String dbContext, String[] cveIds) throws DataAccessException {
        return mirrorService.handleGetCveById(dbContext, cveIds);
    }

    /**
     * Returns, from the specified database, a list of CWEs associated with the provided cveId
     * @param dbContext local or persistent
     * @param cveId This is the official cveId from the NVD
     * @return String array of CWEs
     * @throws DataAccessException
     */
    public static String[] getCwes(String dbContext, String cveId) throws DataAccessException{
        return mirrorService.handleGetCwes(dbContext, cveId);
    }

    /**
     * Gets a single CVE from the National Vulnerability Database. It DOES make an API call.
     * In most cases, it will be much more performant to use the local or persistent database, but this
     * method exists to create direct access to the NVD with a single command.
     * @param cveId This is the official cveId from the NVD
     * @return Requested Cve object
     * @throws ApiCallException
     */
    public static Cve getCveFromNvd(String cveId) throws ApiCallException {
        return nvdApiService.handleGetCveFromNvd(cveId);
    }

    /**
     * Calls to GitHub's Security Advisory Database. Currently, this is optimized for the SBOM Pique Wrapper.
     * In the future, a fully-fledged GraphQL library will be included with this library allowing for type-safe, expressive
     * querying of the GitHub Security Advisory Database as well as other GraphQl endpoints.
     * @param ghsaId the official GHSA ID of interest as published by GitHub
     * @return Returns a SecurityAdvisory object optimized for use in the SBOM wrapper
     * @throws ApiCallException
     */
    public static SecurityAdvisory getGhsa(String ghsaId) throws ApiCallException {
        return ghsaApiService.handleGetGhsa(ghsaId);
    }

}
