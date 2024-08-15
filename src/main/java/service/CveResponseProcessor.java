package service;

import businessObjects.cve.*;
import common.Constants;

import java.util.*;

public class CveResponseProcessor {

    public List<String> extractCweDescriptions(Cve cve) {
        Optional<List<Weakness>> optionalCweList = Optional.of(cve.getWeaknesses().orElse(new ArrayList<>()));
        List<Weakness> cweList = optionalCweList.get();
        List<String> cwes = new ArrayList<>();

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

    public List<Vulnerability> extractVulnerabilities(CveEntity cveEntity) {
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
        List<Vulnerability> vulnerabilities = extractVulnerabilities(cveEntity);
        List<Cve> cves = new ArrayList<>();

        for (Vulnerability vulnerability : vulnerabilities) {
            cves.add(vulnerability.getCve());
        }

        return cves;
    }

    public Map<String, Metrics> extractCvssScores(List<Cve> cves) {
        Map<String, Metrics> processedMetrics = new HashMap<>();
        for (Cve cve : cves) {
            processedMetrics.put(cve.getId(), cve.getMetrics());
        }
        return processedMetrics;
    }
}
