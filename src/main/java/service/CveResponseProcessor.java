package service;

import businessObjects.cve.*;
import common.Constants;

import java.util.ArrayList;
import java.util.List;

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

    public Cve extractSingleCve(CveEntity cveEntity) {
        return cveEntity.getVulnerabilities().get(0).getCve();
    }

    public int extractTotalResults(CveEntity cveEntity) {
        return cveEntity.getTotalResults();
    }

    public ArrayList<Vulnerability> extractVulnerabilities(CveEntity cveEntity) {
        return cveEntity.getVulnerabilities();
    }

    public NvdMirrorMetaData formatNvdMetaData(CveEntity response) {
        NvdMirrorMetaData metaData = new NvdMirrorMetaData();
        metaData.setId(Constants.MONGO_NVD_METADATA_ID);
        metaData.setTotalResults(Integer.toString(response.getTotalResults()));
        metaData.setFormat(response.getFormat());
        metaData.setVersion(response.getVersion());
        metaData.setTimestamp(response.getTimestamp());

        return metaData;
    }

    public List<Cve> extractAllCves(CveEntity cveEntity) {
        ArrayList<Vulnerability> vulnerabilities = extractVulnerabilities(cveEntity);
        List<Cve> cves = new ArrayList<>();

        for (Vulnerability vulnerability : vulnerabilities) {
            cves.add(vulnerability.getCve());
        }

        return cves;
    }
}
