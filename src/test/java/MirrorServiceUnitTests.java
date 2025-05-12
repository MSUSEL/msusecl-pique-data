import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.CveResponseProcessor;

@ExtendWith(MockitoExtension.class)
public class MirrorServiceUnitTests {

    @Mock
    private CveResponseProcessor cveResponseProcessor;
}
