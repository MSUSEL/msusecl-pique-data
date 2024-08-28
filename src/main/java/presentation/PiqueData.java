package presentation;

import businessObjects.cve.Cve;
import businessObjects.cve.Metrics;
import businessObjects.ghsa.SecurityAdvisory;
import exceptions.ApiCallException;
import exceptions.DataAccessException;
import service.*;

import java.util.List;
import java.util.Map;

/**
 * PiqueData serves as the primary class for interacting with this library. It provides methods to work
 * with common data needs in Pique wrappers. These methods are designed to work with multiple database contexts.
 * Currently, these contexts are "local" or "persistent". The local context builds a containerized instance
 * of MongoDB Community Edition and mirrors the NVD there. The persistent context allows members of the SECL
 * to interact with our on-prem mirror of the NVD.
 *
 * If this class does not provide you with the calls you want to make, please speak with Ryan Cummings in the Software
 * Engineering and Cybersecurity Lab. This utility should include most common use cases for data access in the lab. New
 * functionality will be added as needed.
 */
public class PiqueData {
    protected final NvdApiService nvdApiService;
    protected final GhsaApiService ghsaApiService;
    protected final INvdMirrorService mirrorService;
    protected final CveResponseProcessor cveResponseProcessor;

    public PiqueData(NvdApiService nvdApiService, GhsaApiService ghsaApiService, INvdMirrorService mirrorService, CveResponseProcessor cveResponseProcessor) {
        this.nvdApiService = nvdApiService;
        this.ghsaApiService = ghsaApiService;
        this.mirrorService = mirrorService;
        this.cveResponseProcessor = cveResponseProcessor;
    }

    /**
     * Gets a cve from the specified database. (switching on dbContext) This does NOT call the NVD
     * @param cveId This is the official cveId from the NVD
     * @return Returns a Cve object corresponding to the provided cveId
     * @throws DataAccessException
     */
    public Cve getCve(String cveId) throws DataAccessException {
        return mirrorService.handleGetCveById(cveId);
    }

    /**
     * Returns a list of Cve objects from the specified database.
     * @param cveIds String array of cveIds
     * @return Returns an array of CVE objects corresponding to the provided cveIds
     * @throws DataAccessException
     */
    public List<Cve> getCve(List<String> cveIds) throws DataAccessException {
        return mirrorService.handleGetCveById(cveIds);
    }

    /**
     * Returns, from the specified database, a list of CWEs associated with the provided cveId
     * @param cveId This is the official cveId from the NVD
     * @return String array of CWEs
     * @throws DataAccessException
     */
    public List<String> getCweName(String cveId) throws DataAccessException{
        return mirrorService.handleGetNvdCweName(cveId);
    }

    /**
     * Gets a single CVE from the National Vulnerability Database. It DOES make an API call.
     * In most cases, it will be much more performant to use the local or persistent database, but this
     * method exists to create direct access to the NVD with a single command.
     * @param cveId This is the official cveId from the NVD
     * @return Requested Cve object
     */
    public Cve getCveFromNvd(String cveId) throws ApiCallException {
        return cveResponseProcessor.extractSingleCve(nvdApiService.handleGetEntity(cveId));
    }

    /**
     * Calls to GitHub's Security Advisory Database. Currently, this is optimized for the SBOM Pique Wrapper.
     * In the future, a fully-fledged GraphQL library will be included with this library allowing for type-safe, expressive
     * querying of the GitHub Security Advisory Database as well as other GraphQl endpoints.
     * @param ghsaId the official GHSA ID of interest as published by GitHub
     * @return Returns a SecurityAdvisory object optimized for use in the SBOM wrapper
     * @throws ApiCallException
     */
    public SecurityAdvisory getGhsa(String ghsaId) throws ApiCallException {
        return ghsaApiService.handleGetEntity(ghsaId);
    }

    /**
     * Calls GitHub's Security Advisory database and returns a formatted List of CWE names as Strings.
     *
     * @param ghsaId
     * @return
     * @throws ApiCallException
     */
    public List<String> getCweIdsFromGhsa(String ghsaId) throws ApiCallException {
        return ghsaApiService.handleGetCweIdsFromGhsa(ghsaId);
    }

    /**
     * Gets CVSS Metrics for a given CVE. The CVSS Metrics are returned as a java map. The key is
     * the CVE id, and the value is a Metrics object. Each Metrics Object contains Lists of Strings
     * that allow for different CVSS formats. You'll need to check that each list is not empty and
     * use any contained values accordingly.
     *
     * @param cveIds
     * @return
     * @throws DataAccessException
     */
    public Map<String, Metrics> getCvssMetrics(List<String> cveIds) throws DataAccessException {
        return mirrorService.handleGetCvssMetrics(cveIds);
    }
}
