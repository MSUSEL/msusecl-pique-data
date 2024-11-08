package businessObjects.cve;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

@Getter 
@Setter
public final class Cve {
    private String id;
    private String sourceIdentifier;
    private String published;
    private String lastModified;
    private String vulnStatus;
    private List<Description> descriptions;
    private Metrics metrics;
    private List<Weakness> weaknesses;
    private List<Configuration> configurations;
    private List<Reference> references;

    public Optional<List<Weakness>> getWeaknesses() {
        return Optional.ofNullable(weaknesses);
    }
}