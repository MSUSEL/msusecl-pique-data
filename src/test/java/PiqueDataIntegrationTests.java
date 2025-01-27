/*
 * MIT License
 *
 * Copyright (c) 2024 Montana State University Software Engineering and Cybersecurity Laboratory
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import businessObjects.cve.*;
import businessObjects.ghsa.SecurityAdvisory;
import exceptions.ApiCallException;
import exceptions.DataAccessException;
import org.junit.jupiter.api.Test;
import presentation.NvdMirror;
import presentation.PiqueData;
import presentation.PiqueDataFactory;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

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
    public void testGetCve() {
        // Happy path
        assertEquals(TestConstants.CVE_A, piqueData.getCve(TestConstants.CVE_A).getId());
        assertEquals(TestConstants.CVE_B, piqueData.getCve(TestConstants.CVE_B).getId());

        // Bad id with good format
        assertThrows(DataAccessException.class, () -> {
            piqueData.getCve(TestConstants.BAD_CVE_A);
        });

        // Bad input format
        assertThrows(DataAccessException.class, () -> {
            piqueData.getCve(TestConstants.BAD_FORMAT);
        });
    }

    @Test
    public void testGetCves() throws DataAccessException {
        List<String> cveIds = Arrays.asList(TestConstants.CVE_A, TestConstants.CVE_B);

        // Happy path
        assertEquals(TestConstants.CVE_A, piqueData.getCve(cveIds).get(0).getId());
        assertEquals(TestConstants.CVE_B, piqueData.getCve(cveIds).get(1).getId());

        // Single bad id in list
        assertEquals(TestConstants.CVE_B, piqueData.getCve(
                Arrays.asList(TestConstants.BAD_CVE_A, TestConstants.CVE_B)).get(0).getId());

        // All bad ids in list
        assertThrows(DataAccessException.class, () -> piqueData.getCve(
                Arrays.asList(TestConstants.BAD_CVE_A, TestConstants.BAD_CVE_B)));

        // Bad cveId format in one Cve
        assertEquals(TestConstants.CVE_A,
                piqueData.getCve(Arrays.asList(TestConstants.BAD_FORMAT, TestConstants.CVE_A))
                .get(0).getId());
    }

    @Test
    public void testGetCwes() throws DataAccessException {
        List<String> cwes = piqueData.getCweName(TestConstants.CVE_B);

        // Happy path
        assertEquals(TestConstants.CVE_B_CWE_ORACLE, cwes.get(0));

        // TODO Find cve without cwe and ensure that the resulting object is empty
    }

    @Test
    public void testGetCveFromNvd() throws ApiCallException {
        // Happy path
        assertEquals(TestConstants.CVE_A, piqueData.getCveFromNvd(TestConstants.CVE_A).getId());

        // FIXME Index Out of Bounds exception
        // Bad cve id
        assertThrows(DataAccessException.class, () -> piqueData.getCveFromNvd(TestConstants.BAD_CVE_A));

        // Bad format
        assertThrows(DataAccessException.class, () -> piqueData.getCveFromNvd(TestConstants.BAD_FORMAT));
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
        // TODO set up test database to avoid mutating real copy of mirror
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

        // Happy path
        assertEquals(TestConstants.CVE_A, entity.getVulnerabilities().get(0).getCve().getId());
    }

    @Test
    public void testGetCvssScores() throws DataAccessException {
        // Happy path
        List<String> cveIds = Arrays.asList(TestConstants.CVE_A, TestConstants.CVE_B);
        Map<String, Metrics> data = piqueData.getCvssMetrics(cveIds);
        // TODO assertion?!
    }

    @Test
    public void testMarshalMetadataToJson() throws DataAccessException {
        NvdMirrorMetaData metadata = nvdMirror.getMetaData();
        assertNotEquals(null, metadata.getFormat());
        assertNotEquals(null, metadata.getLastTimestamp());
        assertNotEquals(null, metadata.getApiVersion());
        assertNotEquals(null, metadata.getCvesModified());
    }
}
