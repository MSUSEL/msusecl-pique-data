package businessObjects;

import common.Constants;
import persistence.HeaderBuilder;
import common.NvdConstants;
import persistence.ParameterBuilder;
import handlers.IJsonMarshaller;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;

import java.util.List;

public class NvdRequestBuilder {
    private final ParameterBuilder parameterBuilder = new ParameterBuilder();
    private final HeaderBuilder headerBuilder = new HeaderBuilder();
    private final ResponseHandler<String> jsonResponseHandler;
    private final IJsonMarshaller cveEntityMarshaller;

    public NvdRequestBuilder(ResponseHandler<String> jsonResponseHandler, IJsonMarshaller cveMarshaller) {
        this.jsonResponseHandler = jsonResponseHandler;
        this.cveEntityMarshaller = cveMarshaller;
    }

    public NvdRequestBuilder withCveId(String cveId) {
        parameterBuilder.addParameter(NvdConstants.CVE_ID, cveId);
        return this;
    }

    public NvdRequestBuilder withApiKey(String apiKey) {
        headerBuilder.addHeader(NvdConstants.API_KEY, apiKey);
        return this;
    }

    public NvdRequestBuilder withStartIndex(String startIndex) {
        parameterBuilder.addParameter(NvdConstants.START_INDEX, startIndex);
        return this;
    }

    public NvdRequestBuilder withResultsPerPage(String resultsPerPage) {
        parameterBuilder.addParameter(NvdConstants.RESULTS_PER_PAGE, resultsPerPage);
        return this;
    }

    public NvdRequestBuilder withLastModStartEndDates(String lastModStartDate, String lastModEndDate) {
        parameterBuilder.addParameter(NvdConstants.LAST_MOD_START_DATE, lastModStartDate);
        parameterBuilder.addParameter(NvdConstants.LAST_MOD_END_DATE, lastModEndDate);
        return this;
    }

    public NvdRequestBuilder withKeyWordSearch(String keywordSearch) {
        parameterBuilder.addParameter(NvdConstants.KEYWORD_SEARCH, keywordSearch);
        return this;
    }

    public NvdRequestBuilder withKeywordExactMatch(String keywordExactMatch) {
        parameterBuilder.addParameter(NvdConstants.KEYWORD_EXACT_MATCH, keywordExactMatch);
        return this;
    }

    public NvdRequestBuilder withVirtualMatchString(String virtualMatchString) {
        parameterBuilder.addParameter(NvdConstants.VIRTUAL_MATCH_STRING, virtualMatchString);
        return this;
    }

    public NvdRequestBuilder withCpeName(String cpeName) {
        parameterBuilder.addParameter(NvdConstants.CPE_NAME, cpeName);
        return this;
    }

    public NvdRequestBuilder withCveTag(String cveTag) {
        parameterBuilder.addParameter(NvdConstants.CVE_TAG, cveTag);
        return this;
    }

    public NvdRequestBuilder withCvssV2Metrics(String cvssV2Metrics) {
        parameterBuilder.addParameter(NvdConstants.CVSS_V2_METRICS, cvssV2Metrics);
        return this;
    }

    public NvdRequestBuilder withCvssV2Severity(String cvssV2Severity) {
        parameterBuilder.addParameter(NvdConstants.CVSS_V2_SEVERITY, cvssV2Severity);
        return this;
    }

    public NvdRequestBuilder withCvssV3Metrics(String cvssV3Metrics) {
        parameterBuilder.addParameter(NvdConstants.CVSS_V3_METRICS, cvssV3Metrics);
        return this;
    }

    public NvdRequestBuilder withCvssV3Severity(String cvssV3Severity) {
        parameterBuilder.addParameter(NvdConstants.CVSS_V3_SEVERITY, cvssV3Severity);
        return this;
    }

    public NvdRequestBuilder withCvssV4Metrics(String cvssV4Metrics) {
        parameterBuilder.addParameter(NvdConstants.CVSS_V4_METRICS, cvssV4Metrics);
        return this;
    }

    public NvdRequestBuilder withCvssV4Severity(String cvssV4Severity) {
        parameterBuilder.addParameter(NvdConstants.CVSS_V4_SEVERITY, cvssV4Severity);
        return this;
    }

    public NvdRequestBuilder withCweId(String cweId) {
        parameterBuilder.addParameter(NvdConstants.CWE_ID, cweId);
        return this;
    }

    public NvdRequestBuilder withHasCertAlerts(String hasCertAlerts) {
        parameterBuilder.addParameter(NvdConstants.HAS_CERT_ALERTS, hasCertAlerts);
        return this;
    }

    public NvdRequestBuilder withHasCertNotes(String hasCertNotes) {
        parameterBuilder.addParameter(NvdConstants.HAS_CERT_NOTES, hasCertNotes);
        return this;
    }

    public NvdRequestBuilder withHasKev(String hasKev) {
        parameterBuilder.addParameter(NvdConstants.HAS_KEV, hasKev);
        return this;
    }

    public NvdRequestBuilder withHasOval(String hasOval) {
        parameterBuilder.addParameter(NvdConstants.HAS_OVAL, hasOval);
        return this;
    }

    public NvdRequestBuilder withIsVulnerable(String isVulnerable) {
        parameterBuilder.addParameter(NvdConstants.IS_VULNERABLE, isVulnerable);
        return this;
    }

    public NvdRequestBuilder withNoRejected(String noRejected) {
        parameterBuilder.addParameter(NvdConstants.NO_REJECTED, noRejected);
        return this;
    }

    public NvdRequestBuilder withPubStartEndDates(String pubStartDate, String pubEndDate) {
        parameterBuilder.addParameter(NvdConstants.PUB_START_DATE, pubStartDate)
                .addParameter(NvdConstants.PUB_END_DATE, pubEndDate);
        return this;
    }

    public NvdRequestBuilder withSourceIdentifier(String sourceIdentifier) {
        parameterBuilder.addParameter(NvdConstants.SOURCE_IDENTIFIER, sourceIdentifier);
        return this;
    }

    public NvdRequestBuilder withVersionEnd(String versionEnd) {
        parameterBuilder.addParameter(NvdConstants.VERSION_END, versionEnd);
        return this;
    }

    public NvdRequestBuilder withVersionEndType(String versionEndType) {
        parameterBuilder.addParameter(NvdConstants.VERSION_END_TYPE, versionEndType);
        return this;
    }

    public NvdRequestBuilder withFullMirrorDefaults(String startIndex) {
        parameterBuilder.addParameter(NvdConstants.START_INDEX, startIndex)
                .addParameter(NvdConstants.RESULTS_PER_PAGE, Integer.toString(Constants.NVD_MAX_PAGE_SIZE));
        headerBuilder.addHeader(NvdConstants.API_KEY, System.getenv("NVD_KEY"));
        return this;
    }

    public NvdRequest build() {
        List<NameValuePair> params = parameterBuilder.build();
        Header[] headers = headerBuilder.build();

        return new NvdRequest(HTTPMethod.GET, Constants.NVD_CVE_URI, headers, params, jsonResponseHandler, cveEntityMarshaller);
    }
}
