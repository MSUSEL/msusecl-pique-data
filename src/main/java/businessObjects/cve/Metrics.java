package businessObjects.cve;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public final class Metrics {
    private List<CvssMetricV40> cvssMetricV40;
    private List<CvssMetricV31> cvssMetricV31;
    private List<CvssMetricV2> cvssMetricV2;
}