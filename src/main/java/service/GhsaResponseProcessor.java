package service;

import businessObjects.GHSAResponse;
import businessObjects.ghsa.Nodes;
import businessObjects.ghsa.SecurityAdvisory;

import java.util.ArrayList;
import java.util.List;

public final class GhsaResponseProcessor {
    // Methods to handle raw GHSA Response object
    public List<Nodes> extractCweNodes(GHSAResponse ghsaResponse) {
        return ghsaResponse.getEntity().getCwes().getNodes();
    }

    public String extractGhsaId(GHSAResponse ghsaResponse) {
        return ghsaResponse.getEntity().getGhsaId();
    }

    public String extractSummary(GHSAResponse ghsaResponse) {
        return ghsaResponse.getEntity().getSummary();
    }

    // methods to extract fields from Security Advisories
    public List<String> extractCweIds(SecurityAdvisory advisory) {
        List<Nodes> nodes = advisory.getCwes().getNodes();
        List<String> ids = new ArrayList<>();
        for (Nodes node : nodes) {
            ids.add(node.getCweId());
        }
        return ids;
    }
}
