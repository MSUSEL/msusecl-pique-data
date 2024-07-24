package service;

import businessObjects.cve.*;
import common.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a convenience class to avoid littering the code
 * with lots of .get chains.
 */
public class CveResponseProcessor {

    public ArrayList<String> extractCweDescriptions(Cve cve) {
        ArrayList<Weakness> cweList = cve.getWeaknesses();
        ArrayList<String> cwes = new ArrayList<>();

        for (Weakness weakness : cweList) {
            for (WeaknessDescription description : weakness.getDescription()) {
                cwes.add(description.getValue());
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

    public NvdMirrorMetaData formatNvdMetaData(CVEResponse response) {
        NvdMirrorMetaData metaData = new NvdMirrorMetaData();
        metaData.setId(Constants.MONGO_NVD_METADATA_ID);
        metaData.setTotalResults(Integer.toString(response.getTotalResults()));
        metaData.setFormat(response.getFormat());
        metaData.setVersion(response.getVersion());
        metaData.setTimestamp(response.getTimestamp());

        return metaData;
    }

    public List<Cve> extractAllCves(CVEResponse cveResponse) {
        ArrayList<Vulnerability> vulnerabilities = extractVulnerabilities(cveResponse);
        List<Cve> cves = new ArrayList<>();

        for (Vulnerability vulnerability : vulnerabilities) {
            cves.add(vulnerability.getCve());
        }

        return cves;
    }
}
