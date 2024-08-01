import businessObjects.cve.Cve;
import businessObjects.ghsa.SecurityAdvisory;
import common.Constants;
import exceptions.ApiCallException;
import exceptions.DataAccessException;
import org.junit.Test;
import presentation.NvdMirror;
import presentation.PiqueData;
import presentation.PiqueDataFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Integration tests covering PiqueData in the presentation layer
 */

// TODO test edge cases and create more robust asserts
// TODO Create Mocked databases rather than hitting "production"
public class PiqueDataIntegrationTests {
    private final PiqueDataFactory piqueDataFactory = new PiqueDataFactory();
    private final PiqueData piqueData = piqueDataFactory.getPiqueData();

    @Test
    public void testLocalGetCve() throws DataAccessException {
        Cve result = piqueData.getCve(TestConstants.CVE_A);
        assertEquals(TestConstants.CVE_A, result.getId());
    }

    @Test
    public void testPersistentGetCve() throws DataAccessException {
        Cve cve = piqueData.getCve(TestConstants.CVE_B);
        assertEquals(TestConstants.CVE_B, cve.getId());
        assertEquals(TestConstants.CVE_B_CWE_ORACLE, cve.getWeaknesses().get(0).getDescription().get(0).getValue());
    }

    @Test
    public void testGetLocalCvesById() throws DataAccessException {
        List<String> cveIds = Arrays.asList(TestConstants.CVE_A, TestConstants.CVE_B);
        List<Cve> result = piqueData.getCve(cveIds);

        assertEquals(TestConstants.CVE_A, result.get(0).getId());
        assertEquals(TestConstants.CVE_B, result.get(1).getId());
    }

    @Test
    public void testGetPersistentCvesById() throws DataAccessException {
        List<String> cveIds = Arrays.asList(TestConstants.CVE_A, TestConstants.CVE_B);
        List<Cve> result = piqueData.getCve(cveIds);

        assertEquals(TestConstants.CVE_A, result.get(0).getId());
        assertEquals(TestConstants.CVE_B, result.get(1).getId());
    }

    @Test
    public void testGetLocalCwes() throws DataAccessException {
       List<String> cwes = piqueData.getNvdCweDescriptions(TestConstants.CVE_B);

       assertEquals(cwes.get(0), TestConstants.CVE_B_CWE_ORACLE);
    }

    @Test
    public void testGetPersistentCwes() throws DataAccessException {
        ArrayList<String> cwes = piqueData.getNvdCweDescriptions(TestConstants.CVE_B);

        assertEquals(cwes.get(0), TestConstants.CVE_B_CWE_ORACLE);
    }

    @Test
    public void testGetCveFromNvd() throws ApiCallException {
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
