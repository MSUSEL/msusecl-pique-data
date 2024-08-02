package businessObjects.cve;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class CvssData {
    private String version;
    private String vectorString;
    private String accessVector;
    private String attackVector;
    private String attackRequirements;
    private String accessComplexity;
    private String attackComplexity;
    private String authentication;
    private String privilegesRequired;
    private String userInteraction;
    private String vulnerableSystemConfidentiality;
    private String vulnerableSystemIntegrity;
    private String vulnerableSystemAvailability;
    private String subsequentSystemConfidentiality;
    private String subsequentSystemIntegrity;
    private String subsequentSystemAvailability;
    private String exploitMaturity;
    private String scope;
    private String confidentialityImpact;
    private String confidentialityRequirements;
    private String integrityRequirements;
    private String availabilityRequirements;
    private String modifiedAttackVector;
    private String modifiedAttackComplexity;
    private String modifiedAttackRequirements;
    private String modifiedPrivilegesRequired;
    private String modifiedUserInteraction;
    private String modifiedVulnerableSystemConfidentiality;
    private String modifiedVulnerableSystemIntegrity;
    private String modifiedVulnerableSystemAvailability;
    private String modifiedSubsequentSystemConfidentiality;
    private String modifiedSubsequentSystemIntegrity;
    private String modifiedSubsequentSystemAvailability;
    private String safety;
    private String automatable;
    private String recovery;
    private String valueDensity;
    private String vulnerabilityResponseEffort;
    private String providerUrgency;
    private String integrityImpact;
    private String availabilityImpact;
    private String baseScore;
    private String baseSeverity;
}