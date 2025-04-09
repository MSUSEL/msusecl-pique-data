/*
 * MIT License
 *
 * Copyright (c) 2024 Montana State University Software Engineering and Cybersecurity Laboratory
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package service;

import businessObjects.cve.*;

import java.util.*;

public class CveResponseProcessor implements IResponseProcessor {

    @Override
    public List<String> extractCweDescriptions(Cve cve) {
        List<Weakness> cweList = Optional.of(cve.getWeaknesses().orElse(new ArrayList<>())).get();
        List<String> cwes = new ArrayList<>();

        for (Weakness weakness : cweList) {
            for (WeaknessDescription description : weakness.getDescription()) {
                cwes.add(description.getValue());
            }
        }

        return cwes;
    }

    @Override
    public Cve extractSingleCve(CveEntity cveEntity) {
        return cveEntity.getVulnerabilities().get(0).getCve();
    }

    @Override
    public int extractTotalResults(CveEntity cveEntity) {
        return cveEntity.getTotalResults();
    }

    @Override
    public List<Vulnerability> extractVulnerabilities(CveEntity cveEntity) {
        return cveEntity.getVulnerabilities();
    }

    @Override
    public NvdMirrorMetaData extractNvdMetaData(CveEntity response) {
        NvdMirrorMetaData metaData = new NvdMirrorMetaData();

        metaData.setCvesModified(Integer.toString(response.getTotalResults()));
        metaData.setFormat(response.getFormat());
        metaData.setApiVersion(response.getVersion());
        metaData.setLastTimestamp(response.getTimestamp());

        return metaData;
    }

    @Override
    public List<Cve> extractAllCves(CveEntity cveEntity) {
        List<Vulnerability> vulnerabilities = extractVulnerabilities(cveEntity);
        List<Cve> cves = new ArrayList<>();

        for (Vulnerability vulnerability : vulnerabilities) {
            cves.add(vulnerability.getCve());
        }

        return cves;
    }

    @Override
    public Map<String, Metrics> extractCvssScores(List<Cve> cves) {
        Map<String, Metrics> processedMetrics = new HashMap<>();
        for (Cve cve : cves) {
            processedMetrics.put(cve.getId(), cve.getMetrics());
        }
        return processedMetrics;
    }
}
