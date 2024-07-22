import businessObjects.cve.Cve;
import businessObjects.ghsa.SecurityAdvisory;
import common.Constants;
import exceptions.ApiCallException;
import exceptions.DataAccessException;
import org.junit.Test;
import presentation.NvdMirror;
import presentation.PiqueData;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Integration tests covering PiqueData in the presentation layer
 */

// TODO test edge cases and create more robust asserts
// TODO separate local and persistent tests.
// TODO Create Mocked databases rather than hitting "production"
public class PiqueDataIntegrationTests {

    @Test
    public void testLocalGetCveById() throws DataAccessException {
        Cve result = PiqueData.getCveById(Constants.DB_CONTEXT_LOCAL, TestConstants.CVE_A);
        assertEquals(TestConstants.CVE_A, result.getId());
    }

    @Test
    public void testPersistentGetCveById() throws DataAccessException {
        Cve cve = PiqueData.getCveById(Constants.DB_CONTEXT_PERSISTENT, TestConstants.CVE_B);
        assertEquals(TestConstants.CVE_B, cve.getId());
        assertEquals(TestConstants.CVE_B_CWE_ORACLE, cve.getWeaknesses().get(0).getDescription().get(0).getValue());
    }

    @Test
    public void testGetLocalCvesById() throws DataAccessException {
        String[] cveIds = {TestConstants.CVE_A, TestConstants.CVE_B};
        List<Cve> result = PiqueData.getCveById(Constants.DB_CONTEXT_LOCAL, cveIds);

        assertEquals(TestConstants.CVE_A, result.get(0).getId());
        assertEquals(TestConstants.CVE_B, result.get(1).getId());
    }

    @Test
    public void testGetPersistentCvesById() throws DataAccessException {
        String[] cveIds = {TestConstants.CVE_A, TestConstants.CVE_B};
        List<Cve> result = PiqueData.getCveById(Constants.DB_CONTEXT_PERSISTENT, cveIds);

        assertEquals(TestConstants.CVE_A, result.get(0).getId());
        assertEquals(TestConstants.CVE_B, result.get(1).getId());
    }

    @Test
    public void testGetLocalCwes() throws DataAccessException {
       ArrayList<String> cwes = PiqueData.getNvdCweDescriptions(Constants.DB_CONTEXT_LOCAL, TestConstants.CVE_B);

       assertEquals(cwes.get(0), TestConstants.CVE_B_CWE_ORACLE);
    }

    @Test
    public void testGetPersistentCwes() throws DataAccessException {
        ArrayList<String> cwes = PiqueData.getNvdCweDescriptions(Constants.DB_CONTEXT_PERSISTENT, TestConstants.CVE_B);

        assertEquals(cwes.get(0), TestConstants.CVE_B_CWE_ORACLE);
    }

    @Test
    public void testGetCveFromNvd() {
        Cve result = PiqueData.getCveFromNvd(TestConstants.CVE_A);

        assertEquals(TestConstants.CVE_A, result.getId());
    }

    @Test
    public void testGetGHSA() throws ApiCallException {
        SecurityAdvisory result = PiqueData.getGhsa(TestConstants.GHSA_ID_A);

        assertEquals(TestConstants.GHSA_ID_A, result.getGhsaId());
    }

    @Test
    public void testPersistentDeleteCve() throws DataAccessException {
        NvdMirror.deleteSingleCve(Constants.DB_CONTEXT_PERSISTENT, TestConstants.CVE_A);
    }

}
