package service;

import businessObjects.GHSAResponse;
import businessObjects.ghsa.Nodes;
import businessObjects.ghsa.SecurityAdvisory;

import java.util.List;

public interface ISbomGhsaResponseProcessor {
    List<Nodes> extractCweNodes(GHSAResponse ghsaResponse);
    String extractGhsaId(GHSAResponse ghsaResponse);
    String extractSummary(GHSAResponse ghsaResponse);
    List<String> extractCweIds(SecurityAdvisory advisory);
}
