package service;

import businessObjects.GHSAResponse;
import businessObjects.ghsaData.CweNode;

import java.util.ArrayList;

public class GhsaResponseProcessor {
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
