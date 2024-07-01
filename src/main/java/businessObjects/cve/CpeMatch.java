package businessObjects.cve;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CpeMatch {
    private String vulnerable;
    private String criteria;
    private String matchCriteriaId;
}