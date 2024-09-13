package businessObjects;

import common.Constants;
import persistence.HeaderBuilder;
import common.NvdConstants;
import persistence.NvdParameterBuilder;
import handlers.IJsonMarshaller;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;

import java.util.List;

public class NvdRequestBuilder {
    private final NvdParameterBuilder nvdParameterBuilder = new NvdParameterBuilder();
    private final HeaderBuilder headerBuilder = new HeaderBuilder();
    private final ResponseHandler<String> jsonResponseHandler;
    private final IJsonMarshaller cveEntityMarshaller;

    public NvdRequestBuilder(ResponseHandler<String> jsonResponseHandler, IJsonMarshaller cveMarshaller) {
        this.jsonResponseHandler = jsonResponseHandler;
        this.cveEntityMarshaller = cveMarshaller;
    }

    public NvdRequestBuilder withCveId(String cveId) {
        nvdParameterBuilder.addParameter(NvdConstants.CVE_ID, cveId);
        return this;
    }

    public NvdRequestBuilder withApiKey(String apiKey) {
        headerBuilder.addHeader(NvdConstants.API_KEY, apiKey);
        return this;
    }

    public NvdRequestBuilder withStartIndex(String startIndex) {
        nvdParameterBuilder.addParameter(NvdConstants.START_INDEX, startIndex);
        return this;
    }

    public NvdRequestBuilder withResultsPerPage(String resultsPerPage) {
        nvdParameterBuilder.addParameter(NvdConstants.RESULTS_PER_PAGE, resultsPerPage);
        return this;
    }

    public NvdRequestBuilder withLastModStartEndDates(String lastModStartDate, String lastModEndDate) {
        nvdParameterBuilder.addParameter(NvdConstants.LAST_MOD_START_DATE, lastModStartDate);
        nvdParameterBuilder.addParameter(NvdConstants.LAST_MOD_END_DATE, lastModEndDate);
        return this;
    }

    public NvdRequestBuilder withKeyWordSearch(String keywordSearch) {
        nvdParameterBuilder.addParameter(NvdConstants.KEYWORD_SEARCH, keywordSearch);
        return this;
    }

    public NvdRequestBuilder withKeywordExactMatch(String keywordExactMatch) {
        nvdParameterBuilder.addParameter(NvdConstants.KEYWORD_EXACT_MATCH, keywordExactMatch);
        return this;
    }

    public NvdRequestBuilder withVirtualMatchString(String virtualMatchString) {
        nvdParameterBuilder.addParameter(NvdConstants.VIRTUAL_MATCH_STRING, virtualMatchString);
        return this;
    }

    public NvdRequestBuilder withCpeName(String cpeName) {
        nvdParameterBuilder.addParameter(NvdConstants.CPE_NAME, cpeName);
        return this;
    }

    public NvdRequestBuilder withCveTag(String cveTag) {
        nvdParameterBuilder.addParameter(NvdConstants.CVE_TAG, cveTag);
        return this;
    }

    public NvdRequestBuilder withCvssV2Metrics(String cvssV2Metrics) {
        nvdParameterBuilder.addParameter(NvdConstants.CVSS_V2_METRICS, cvssV2Metrics);
        return this;
    }

    public NvdRequestBuilder withCvssV2Severity(String cvssV2Severity) {
        nvdParameterBuilder.addParameter(NvdConstants.CVSS_V2_SEVERITY, cvssV2Severity);
        return this;
    }

    public NvdRequestBuilder withCvssV3Metrics(String cvssV3Metrics) {
        nvdParameterBuilder.addParameter(NvdConstants.CVSS_V3_METRICS, cvssV3Metrics);
        return this;
    }

    public NvdRequestBuilder withCvssV3Severity(String cvssV3Severity) {
        nvdParameterBuilder.addParameter(NvdConstants.CVSS_V3_SEVERITY, cvssV3Severity);
        return this;
    }

    public NvdRequestBuilder withCvssV4Metrics(String cvssV4Metrics) {
        nvdParameterBuilder.addParameter(NvdConstants.CVSS_V4_METRICS, cvssV4Metrics);
        return this;
    }

    public NvdRequestBuilder withCvssV4Severity(String cvssV4Severity) {
        nvdParameterBuilder.addParameter(NvdConstants.CVSS_V4_SEVERITY, cvssV4Severity);
        return this;
    }

    public NvdRequestBuilder withCweId(String cweId) {
        nvdParameterBuilder.addParameter(NvdConstants.CWE_ID, cweId);
        return this;
    }

    public NvdRequestBuilder withHasCertAlerts(String hasCertAlerts) {
        nvdParameterBuilder.addParameter(NvdConstants.HAS_CERT_ALERTS, hasCertAlerts);
        return this;
    }

    public NvdRequestBuilder withHasCertNotes(String hasCertNotes) {
        nvdParameterBuilder.addParameter(NvdConstants.HAS_CERT_NOTES, hasCertNotes);
        return this;
    }

    public NvdRequestBuilder withHasKev(String hasKev) {
        nvdParameterBuilder.addParameter(NvdConstants.HAS_KEV, hasKev);
        return this;
    }

    public NvdRequestBuilder withHasOval(String hasOval) {
        nvdParameterBuilder.addParameter(NvdConstants.HAS_OVAL, hasOval);
        return this;
    }

    public NvdRequestBuilder withIsVulnerable(String isVulnerable) {
        nvdParameterBuilder.addParameter(NvdConstants.IS_VULNERABLE, isVulnerable);
        return this;
    }

    public NvdRequestBuilder withNoRejected(String noRejected) {
        nvdParameterBuilder.addParameter(NvdConstants.NO_REJECTED, noRejected);
        return this;
    }

    public NvdRequestBuilder withPubStartEndDates(String pubStartDate, String pubEndDate) {
        nvdParameterBuilder.addParameter(NvdConstants.PUB_START_DATE, pubStartDate)
                .addParameter(NvdConstants.PUB_END_DATE, pubEndDate);
        return this;
    }

    public NvdRequestBuilder withSourceIdentifier(String sourceIdentifier) {
        nvdParameterBuilder.addParameter(NvdConstants.SOURCE_IDENTIFIER, sourceIdentifier);
        return this;
    }

    public NvdRequestBuilder withVersionEnd(String versionEnd) {
        nvdParameterBuilder.addParameter(NvdConstants.VERSION_END, versionEnd);
        return this;
    }

    public NvdRequestBuilder withVersionEndType(String versionEndType) {
        nvdParameterBuilder.addParameter(NvdConstants.VERSION_END_TYPE, versionEndType);
        return this;
    }

    public NvdRequestBuilder withFullMirrorDefaults(String startIndex) {
        nvdParameterBuilder.addParameter(NvdConstants.START_INDEX, startIndex)
                .addParameter(NvdConstants.RESULTS_PER_PAGE, Integer.toString(Constants.NVD_MAX_PAGE_SIZE));
        headerBuilder.addHeader(NvdConstants.API_KEY, System.getenv("NVD_KEY"));
        return this;
    }

    public NvdRequest build() {
        List<NameValuePair> params = nvdParameterBuilder.build();
        Header[] headers = headerBuilder.build();

        return new NvdRequest(HTTPMethod.GET, Constants.NVD_CVE_URI, headers, params, jsonResponseHandler, cveEntityMarshaller);
    }
}
