package businessObjects.cve;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class NvdMirrorMetaData {
    private String id;
    private String totalResults;
    private String format;
    private String version;
    private String timestamp;
}
