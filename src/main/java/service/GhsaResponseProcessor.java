package service;

import businessObjects.GHSAResponse;
import businessObjects.ghsa.CweNode;

import java.util.ArrayList;

public final class GhsaResponseProcessor {
    public ArrayList<CweNode> extractCweNodes(GHSAResponse ghsaResponse) {
        return ghsaResponse.getSecurityAdvisory().getCwes().getNodes();
    }

    public String extractGhsaId(GHSAResponse ghsaResponse) {
        return ghsaResponse.getSecurityAdvisory().getGhsaId();
    }

    public String extractSummary(GHSAResponse ghsaResponse) {
        return ghsaResponse.getSecurityAdvisory().getSummary();
    }
}
