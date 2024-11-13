/*
 * MIT License
 *
 * Copyright (c) 2024 Montana State University Software Engineering and Cybersecurity Laboratory
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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