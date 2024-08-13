package businessObjects.cve;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CvssMetricV31 {
    private String source;
    private String type;
    private CvssData cvssData;
    private String exploitabilityScore;
    private String impactScore;
}
