package service;

import businessObjects.cve.*;

import java.util.List;
import java.util.Map;

public interface IResponseProcessor {
    List<String> extractCweDescriptions(Cve cve);
    Cve extractSingleCve(CveEntity cveEntity);
    int extractTotalResults(CveEntity cveEntity);
    List<Vulnerability> extractVulnerabilities(CveEntity cveEntity);
    List<Cve> extractAllCves(CveEntity cveEntity);
    Map<String, Metrics> extractCvssScores(List<Cve> cves);
    NvdMirrorMetaData extractNvdMetaData(CveEntity response);
}
