package businessObjects.ghsa;

import businessObjects.baseClasses.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public class WebPiqueSecurityAdvisory extends BaseEntity {
    private String ghsaId;
    private String summary;
    private Cwes cwes;
    private String cve;


    /**
     *
     * @return Optional that will contain the CVE alias for the ghsaId if it exists in the GHSA Database
     */
    public Optional<String> getCve() {
        return Optional.ofNullable(cve);
    }
}
