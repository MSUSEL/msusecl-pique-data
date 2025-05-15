import businessObjects.cve.Cve;
import businessObjects.cve.NvdMirrorMetaData;
import handlers.ICveResponseProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import persistence.IDao;
import persistence.IMetaDataDao;
import service.MirrorService;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MirrorServiceUnitTests {

    @Mock
    private ICveResponseProcessor cveResponseProcessor;

    @Mock
    private IDao<Cve> cveDao;

    @Mock
    private IMetaDataDao<NvdMirrorMetaData> metaDataDao;

    @InjectMocks
    private MirrorService mirrorService;


    @Test
    public void testHandleGetSingleCveById() {
        String cveId = TestConstants.CVE_A;
        TestUtils testUtils = new TestUtils();

        String testCveJson = testUtils.readCveJsonFile(Paths.get("src/test/resources/test.json"));
        Cve testCve = testUtils.deserializeTestData(testCveJson);
        when(cveDao.fetch(Collections.singletonList(cveId))).thenReturn(Collections.singletonList(testCve));

        // Happy path
        Optional<Cve> happyResult = mirrorService.handleGetCveById(TestConstants.CVE_A);

        if (happyResult.isPresent()) {
            assertEquals(TestConstants.CVE_A, happyResult.get().getId());
        } else {
            fail();
        }

        // Empty Optional in the case of no matching CVE for given CVE ID
        Optional<Cve> emptyOptional = mirrorService.handleGetCveById(TestConstants.BAD_CVE_A);
        assert(emptyOptional.isEmpty());
    }

    @Test
    public void testHandleGetMultipleCveById() {
        // TODO: Get json response for list of Cve IDs
        // TODO: Read in that json response and format as list of Cves
        // TODO: Test happy path

    }

    @Test
    public void testHandleGetNvdCweName() {

    }

    @Test
    public void testHandleGetCurrentMetaData() {

    }


    @Test
    public void testHandleInsertSingleCve() {

    }

    @Test
    public void testHandleGetCvssMetrics() {

    }
}
