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
import exceptions.ApiCallException;
import exceptions.DataAccessException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import presentation.NvdMirror;
import presentation.PiqueData;
import presentation.PiqueDataFactory;

import java.util.*;

import static common.Constants.DEFAULT_CREDENTIALS_FILE_PATH;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests covering PiqueData in the presentation layer
 */

// TODO Create Mocked databases rather than hitting "production"
public class PiqueDataTests {
    private final PiqueDataFactory piqueDataFactory = new PiqueDataFactory(DEFAULT_CREDENTIALS_FILE_PATH);
    private final PiqueData piqueData = piqueDataFactory.getPiqueData();
    private final NvdMirror nvdMirror = piqueDataFactory.getNvdMirror();

    @Tag("regression")
    @Test
    public void testGetCve() {
        // Happy path
        Optional<Cve> cveA = piqueData.getCve(TestConstants.CVE_A);
        Optional<Cve> cveB = piqueData.getCve(TestConstants.CVE_B);

        if (cveA.isPresent()) {
            assertEquals(TestConstants.CVE_A, cveA.get().getId());
        } else {
            fail();
        }

        if (cveB.isPresent()) {
            assertEquals(TestConstants.CVE_B, cveB.get().getId());
        } else {
            fail();
        }

        // Bad id with good format
        assertThrows(DataAccessException.class, () -> {
            piqueData.getCve(TestConstants.BAD_CVE_A);
        });

        // Bad input format
        assertThrows(DataAccessException.class, () -> {
            piqueData.getCve(TestConstants.BAD_FORMAT);
        });
    }

    @Tag("regression")
    @Test
    public void testGetCves() throws DataAccessException {
        List<String> cveIds = Arrays.asList(TestConstants.CVE_A, TestConstants.CVE_B);

        // Happy path
        assertEquals(TestConstants.CVE_A, piqueData.getCve(cveIds).get(0).getId());
        assertEquals(TestConstants.CVE_B, piqueData.getCve(cveIds).get(1).getId());

        // Single bad id in list
        assertEquals(
                TestConstants.CVE_B, piqueData.getCve(
                Arrays.asList(TestConstants.BAD_CVE_A, TestConstants.CVE_B)).get(0).getId());

        // All bad ids in list
        assertThrows(
                DataAccessException.class, () -> piqueData.getCve(
                Arrays.asList(TestConstants.BAD_CVE_A, TestConstants.BAD_CVE_B)));

        // Bad cveId format in one Cve
        assertEquals(
                TestConstants.CVE_A,
                piqueData.getCve(Arrays.asList(TestConstants.BAD_FORMAT, TestConstants.CVE_A))
                .get(0).getId());
    }

    @Tag("regression")
    @Test
    public void testGetCwes() throws DataAccessException {
        // Happy path
        assertEquals(
                TestConstants.CVE_B_CWE_ORACLE,
                piqueData.getCweName(TestConstants.CVE_B).get(0));

        // TODO Find cve without cwe and ensure that the resulting object is empty


        Optional<Cve> cveResult = piqueData.getCve(TestConstants.BAD_CVE_A);
        System.out.println(cveResult.isPresent() ? cveResult.get() : "No result returned from database");

//        List<String> result = piqueData.getCweName(TestConstants.BAD_FORMAT);
//        for(String str : result) {
//            System.out.println(str);
//        }
    }

    @Tag("api")
    @Test
    public void testGetCveFromNvd() throws ApiCallException {
        // Happy path
        assertEquals(TestConstants.CVE_A, piqueData.getCveFromNvd(TestConstants.CVE_A).getId());

        // Bad cve id
        assertThrows(
                ApiCallException.class,
                () -> piqueData.getCveFromNvd(TestConstants.BAD_CVE_A));

        // Bad format
        assertThrows(
                ApiCallException.class,
                () -> piqueData.getCveFromNvd(TestConstants.BAD_FORMAT));
    }

    @Tag("api")
    @Test
    public void testGetGHSA() throws ApiCallException {
        assertEquals(
                TestConstants.GHSA_ID_A,
                piqueData.getGhsa(TestConstants.GHSA_ID_A).getGhsaId());

        assertEquals(
                TestConstants.GHSA_ID_B,
                piqueData.getGhsa(TestConstants.GHSA_ID_B).getGhsaId());
    }

    @Tag("api")
    @Test
    public void testGetCweIdsFromGhsa() throws ApiCallException {
        assertEquals(
                TestConstants.GHSA_CWE_A_ORACLE,
                piqueData.getCweIdsFromGhsa(TestConstants.GHSA_ID_A).get(0));
    }

    @Tag("api")
    @Test
    public void testCustomRequestBuilder() throws ApiCallException {
        CveEntity entity = piqueDataFactory.getNvdRequestBuilder()
                .withApiKey(System.getenv("NVD_KEY"))
                .withCpeName("cpe:2.3:a:eric_allman:sendmail:5.58:*:*:*:*:*:*:*")
                .build().executeRequest().getEntity();

        // Happy path
        assertEquals(
                TestConstants.CVE_A,
                entity.getVulnerabilities().get(0).getCve().getId());
    }

    @Tag("regression")
    @Test
    public void testGetCvssScores() throws DataAccessException {
        // Happy path
        assertEquals(
                "nvd@nist.gov",
                piqueData.getCvssMetrics(
                        Arrays.asList(TestConstants.CVE_A, TestConstants.CVE_B))
                        .get(TestConstants.CVE_A)
                        .getCvssMetricV2()
                        .get(0).getSource());

        // Bad cve id
        assertThrows(
                DataAccessException.class, () -> piqueData.getCvssMetrics(
                Collections.singletonList(TestConstants.BAD_CVE_A)));

        // Bad cve id format
        assertThrows(
                DataAccessException.class, () -> piqueData.getCvssMetrics(
                Collections.singletonList(TestConstants.BAD_FORMAT)));

    }

    @Tag("regression")
    @Test
    public void testMarshalMetadataToJson() throws DataAccessException {
        NvdMirrorMetaData metadata = nvdMirror.getMetaData();
        assertNotEquals(null, metadata.getFormat());
        assertNotEquals(null, metadata.getLastTimestamp());
        assertNotEquals(null, metadata.getApiVersion());
        assertNotEquals(null, metadata.getCvesModified());
    }
}
