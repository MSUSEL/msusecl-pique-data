package businessObjects.ghsa;

import businessObjects.baseClasses.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * This class defines the root-level object representing a SecurityAdvisory Response
 * from the GHSA for our query. If we use GraphQL in greater depth, then this
 * should be paired with a schema and library to verify type correctness
 */
@Getter
@Setter
public final class SecurityAdvisory extends BaseEntity {
    private String ghsaId;
    private String summary;
    private Cwes cwes;
}
