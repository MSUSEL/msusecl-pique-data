package businessObjects.cve;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class NvdMirrorMetaData {
    private String cvesModified;
    private String format;
    private String apiVersion;
    private String lastTimestamp;
}
