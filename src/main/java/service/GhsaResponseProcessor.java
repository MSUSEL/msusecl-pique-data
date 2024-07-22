package service;

import businessObjects.GHSAResponse;
import businessObjects.ghsa.CweNode;
import businessObjects.ghsa.Cwes;
import businessObjects.ghsa.SecurityAdvisory;

import java.util.ArrayList;
import java.util.List;

public final class GhsaResponseProcessor {
    // Methods to handle raw GHSA Response object
    public ArrayList<CweNode> extractCweNodes(GHSAResponse ghsaResponse) {
        return ghsaResponse.getSecurityAdvisory().getCwes().getNodes();
    }

    public String extractGhsaId(GHSAResponse ghsaResponse) {
        return ghsaResponse.getSecurityAdvisory().getGhsaId();
    }

    public String extractSummary(GHSAResponse ghsaResponse) {
        return ghsaResponse.getSecurityAdvisory().getSummary();
    }

    // methods to extract fields from Security Advisories
    public ArrayList<String> extractCweIds(SecurityAdvisory advisory) {
        List<CweNode> nodes = advisory.getCwes().getNodes();
        ArrayList<String> ids = new ArrayList<>();
        for (CweNode node : nodes) {
            ids.add(node.getCweId());
        }
        return ids;
    }
}
