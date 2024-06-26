package service;

import api.cveData.CVEResponse;
import api.cveData.Cve;
import api.cveData.Vulnerability;
import api.cveData.Weakness;

import java.util.ArrayList;

public class CveResponseProcessor {

    public String[] extractCwes(Cve cve) {
        ArrayList<Weakness> cweList = cve.getWeaknesses();

        int size = cweList.size();
        String[] cwes = new String[size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < cweList.get(i).getDescription().size(); j++) {
                cwes[i] = cweList.get(i).getDescription().get(j).getValue();
            }
        }

        return cwes;
    }

    public Cve extractSingleCve(CVEResponse cveResponse) {
        return cveResponse.getVulnerabilities().get(0).getCve();
    }

    public int extractTotalResults(CVEResponse cveResponse) {
        return cveResponse.getTotalResults();
    }

    public ArrayList<Vulnerability> extractVulnerabilities(CVEResponse cveResponse) {
        return cveResponse.getVulnerabilities();
    }
}
