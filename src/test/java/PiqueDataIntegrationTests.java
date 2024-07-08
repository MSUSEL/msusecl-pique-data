import businessObjects.cve.Cve;
import businessObjects.ghsa.SecurityAdvisory;
import common.Constants;
import common.DataUtilityProperties;
import exceptions.ApiCallException;
import exceptions.DataAccessException;
import org.junit.Test;
import presentation.NvdMirror;
import presentation.PiqueData;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Integration tests covering PiqueData in the presentation layer
 */

// TODO test edge cases and create more robust asserts
public class PiqueDataIntegrationTests {
    private final Properties prop = DataUtilityProperties.getProperties();
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
        Cve cve = PiqueData.getCveById(Constants.DB_CONTEXT_PERSISTENT, CVE_A);
        NvdMirror.insertSingleCve(Constants.DB_CONTEXT_PERSISTENT, cve);
    }

    @Test
    public void testGetCvesById() throws DataAccessException {
        String[] cveIds = {CVE_A, CVE_B};
        Cve[] result = PiqueData.getCveById(Constants.DB_CONTEXT_LOCAL, cveIds);

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

}
