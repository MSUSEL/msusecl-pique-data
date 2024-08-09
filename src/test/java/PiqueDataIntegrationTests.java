import businessObjects.NvdRequest;
import businessObjects.cve.Cve;
import businessObjects.cve.CveEntity;
import businessObjects.cve.Metrics;
import businessObjects.cve.NvdMirrorMetaData;
import businessObjects.ghsa.SecurityAdvisory;
import exceptions.ApiCallException;
import exceptions.DataAccessException;
import org.junit.Test;
import presentation.NvdMirror;
import presentation.PiqueData;
import presentation.PiqueDataFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Integration tests covering PiqueData in the presentation layer
 */

// TODO test edge cases and create more robust asserts
// TODO Create Mocked databases rather than hitting "production"
public class PiqueDataIntegrationTests {
    private final PiqueDataFactory piqueDataFactory = new PiqueDataFactory();
    private final PiqueData piqueData = piqueDataFactory.getPiqueData();
    private final NvdMirror nvdMirror = piqueDataFactory.getNvdMirror();

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
       List<String> cwes = piqueData.getCweDescriptions(TestConstants.CVE_B);

       assertEquals(cwes.get(0), TestConstants.CVE_B_CWE_ORACLE);
    }

    @Test
    public void testGetPersistentCwes() throws DataAccessException {
        List<String> cwes = piqueData.getCweDescriptions(TestConstants.CVE_B);

        assertEquals(cwes.get(0), TestConstants.CVE_B_CWE_ORACLE);
    }

    @Test
    public void testGetCveFromNvd() throws ApiCallException {
        Cve result = piqueData.getCveFromNvd(TestConstants.CVE_A);

        assertEquals(TestConstants.CVE_A, result.getId());
    }

    @Test
    public void testGetGHSA() throws ApiCallException {
        SecurityAdvisory result1 = piqueData.getGhsa(TestConstants.GHSA_ID_A);
        SecurityAdvisory result2 = piqueData.getGhsa(TestConstants.GHSA_ID_B);

        assertEquals(TestConstants.GHSA_ID_A, result1.getGhsaId());
        assertEquals(TestConstants.GHSA_ID_B, result2.getGhsaId());
    }

    @Test
    public void testPersistentDeleteCve() throws DataAccessException {
        nvdMirror.deleteSingleCve(TestConstants.CVE_A);
    }

    @Test
    public void testGetCweIdsFromGhsa() throws ApiCallException {
        List<String> cweIds = piqueData.getCweIdsFromGhsa(TestConstants.GHSA_ID_A);
        assertEquals(TestConstants.GHSA_CWE_A_ORACLE, cweIds.get(0));
    }

    @Test
    public void testCustomRequestBuilder() throws ApiCallException {
        CveEntity entity = piqueDataFactory.getNvdRequestBuilder()
                .withApiKey(System.getenv("NVD_KEY"))
                .withCpeName("cpe:2.3:a:eric_allman:sendmail:5.58:*:*:*:*:*:*:*")
                .build().executeRequest().getEntity();

        assertEquals(TestConstants.CVE_A, entity.getVulnerabilities().get(0).getCve().getId());
    }

    @Test
    public void testGetCvssScores() throws DataAccessException {
        List<String> cveIds = Arrays.asList(TestConstants.CVE_A, TestConstants.CVE_B);
        Map<String, Metrics> data = piqueData.getCvssMetrics(cveIds);
    }

    @Test
    public void testMarshalMetadataToJson() throws DataAccessException {
        NvdMirrorMetaData metadata = nvdMirror.getMetaData();

    }
}
