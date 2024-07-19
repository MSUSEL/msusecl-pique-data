import businessObjects.cve.Cve;
import businessObjects.ghsa.SecurityAdvisory;
import common.Constants;
import exceptions.ApiCallException;
import exceptions.DataAccessException;
import org.junit.Test;
import presentation.NvdMirror;
import presentation.PiqueData;

import static org.junit.Assert.assertEquals;

/**
 * Integration tests covering PiqueData in the presentation layer
 */

// TODO test edge cases and create more robust asserts
// TODO separate local and persistent tests.
// TODO Create Mocked databases rather than hitting "production"
public class PiqueDataIntegrationTests {
    private final String CVE_A = "CVE-1999-0095";
    private final String CVE_B = "CVE-1999-1302";
    private final String CVE_A_CWE_ORACLE = "NVD-CWE-Other";
    private final String CVE_B_CWE_ORACLE = "NVD-CWE-noinfo";
    private final String GHSA_ID_A = "GHSA-53q7-4874-24qg";

    @Test
    public void testLocalGetCveById() throws DataAccessException {
        Cve result = PiqueData.getCveById(Constants.DB_CONTEXT_LOCAL, CVE_A);
        assertEquals(CVE_A, result.getId());
    }

    @Test
    public void testPersistentGetCveById() throws DataAccessException {
        Cve cve = PiqueData.getCveById(Constants.DB_CONTEXT_PERSISTENT, CVE_B);
        assertEquals(CVE_B, cve.getId());
        assertEquals(CVE_B_CWE_ORACLE, cve.getWeaknesses().get(0).getDescription().get(0).getValue());
    }

    @Test
    public void testGetLocalCvesById() throws DataAccessException {
        String[] cveIds = {CVE_A, CVE_B};
        Cve[] result = PiqueData.getCveById(Constants.DB_CONTEXT_LOCAL, cveIds);

        assertEquals(CVE_A, result[0].getId());
        assertEquals(CVE_B, result[1].getId());
    }

    @Test
    public void testGetPersistentCvesById() throws DataAccessException {
        String[] cveIds = {CVE_A, CVE_B};
        Cve[] result = PiqueData.getCveById(Constants.DB_CONTEXT_PERSISTENT, cveIds);

        assertEquals(CVE_A, result[0].getId());
        assertEquals(CVE_B, result[1].getId());
    }

    @Test
    public void testGetCwes() throws DataAccessException {
       String[] cwes = PiqueData.getCwes(Constants.DB_CONTEXT_LOCAL, CVE_B);

       assertEquals(cwes[0], CVE_B_CWE_ORACLE);
    }

    @Test
    public void testGetCveFromNvd() throws ApiCallException {
        Cve result = PiqueData.getCveFromNvd(CVE_A);

        assertEquals(CVE_A, result.getId());
    }

    @Test
    public void testGetGHSA() throws ApiCallException {
        SecurityAdvisory result = PiqueData.getGhsa(GHSA_ID_A);

        assertEquals(GHSA_ID_A, result.getGhsaId());
    }

    @Test
    public void testPersistentDeleteCve() throws DataAccessException {
        NvdMirror.deleteSingleCve(Constants.DB_CONTEXT_PERSISTENT, CVE_A);
    }

}
