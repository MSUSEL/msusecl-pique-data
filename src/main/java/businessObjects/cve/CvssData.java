package businessObjects.cve;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class CvssData {
    private String version;
    private String vectorString;
    private String accessVector;
    private String accessComplexity;
    private String authentication;
    private String confidentialityImpact;
    private String integrityImpact;
    private String availabilityImpact;
    private Double baseScore;
}