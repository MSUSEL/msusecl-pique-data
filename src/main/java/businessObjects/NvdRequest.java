package businessObjects;

import businessObjects.baseClasses.BaseRequest;
import businessObjects.ghsa.Cwes;
import businessObjects.interfaces.HTTPMethod;
import businessObjects.interfaces.IRequest;
import common.Constants;
import common.HeaderBuilder;
import common.NvdConstants;
import common.ParameterBuilder;
import handlers.JsonResponseHandler;
import handlers.NvdCveMarshaller;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * Inherits from Request and is used to execute GET requests against
 * the National Vulnerabilities Database
 */
public final class NvdRequest extends BaseRequest implements IRequest {
    private Optional<String> cveId = Optional.empty();
    private Optional<String> apiKey = Optional.empty();
    private Optional<String> startIndex = Optional.empty();
    private Optional<String> resultsPerPage = Optional.empty();
    private Optional<String> lastModStartDate = Optional.empty();
    private Optional<String> lastModEndDate = Optional.empty();
    private Optional<String> keywordSearch = Optional.empty();
    private Optional<String> keywordExactMatch = Optional.empty();
    private Optional<String> virtualMatchString = Optional.empty();
    private Optional<String> cpeName = Optional.empty();
    private Optional<String> cveTag = Optional.empty();
    private Optional<String> cvssV2Metrics = Optional.empty();
    private Optional<String> cvssV2Severity= Optional.empty();
    private Optional<String> cvssV3Metrics = Optional.empty();
    private Optional<String> cvssV3Severity= Optional.empty();
    private Optional<String> cvssV4Metrics = Optional.empty();
    private Optional<String> cvssV4Severity= Optional.empty();
    private Optional<String> cweId = Optional.empty();
    private Optional<String> hasCertAlerts = Optional.empty();
    private Optional<String> hasCertNotes = Optional.empty();
    private Optional<String> hasKev = Optional.empty();
    private Optional<String> hasOval = Optional.empty();
    private Optional<String> isVulnerable = Optional.empty();
    private Optional<String> noRejected = Optional.empty();
    private Optional<String> pubStartDate = Optional.empty();
    private Optional<String> pubEndDate = Optional.empty();
    private Optional<String> sourceIdentifier = Optional.empty();
    private Optional<String> versionEnd = Optional.empty();
    private Optional<String> versionEndType = Optional.empty();


    private static final Logger LOGGER = LoggerFactory.getLogger(NvdRequest.class);


//    public NvdRequest(String httpMethod, String baseURI, Header[] headers, List<NameValuePair> params) {
//        super(httpMethod, baseURI, headers);
//        this.params = params;
//    }
    private NvdRequest(NvdRequestBuilder builder) {
        super(builder.httpMethod, builder.baseUri, builder.headers);
        this.cveId = Optional.ofNullable(builder.cveId);
        this.apiKey = Optional.ofNullable(builder.apiKey);
    }

    public static class NvdRequestBuilder {
        private final ParameterBuilder parameterBuilder = new ParameterBuilder();
        private final HeaderBuilder headerBuilder = new HeaderBuilder();

        private String httpMethod;
        private String baseUri;
        private Header[] headers;
        private List<NameValuePair> params;
        private String cveId;
        private String apiKey;
        private String startIndex;
//        private String resultsPerPage;
//        private String lastModStartDate;
//        private String lastModEndDate;
//        private String keywordSearch;
//        private String keywordExactMatch;
//        private String virtualMatchString;
//        private String cpeName;
//        private String cveTag;
//        private String cvssV2Metrics;
//        private String cvssV2Severity;
//        private String cvssV3Metrics;
//        private String cvssV3Severity;
//        private String cvssV4Metrics;
//        private String cvssV4Severity;
//        private String cweId;
//        private String hasCertAlerts;
//        private String hasCertNotes;
//        private String hasKev;
//        private String hasOval;
//        private String isVulnerable;
//        private String noRejected;
//        private String pubStartDate;
//        private String pubEndDate;
//        private String sourceIdentifier;
//        private String versionEnd;
//        private String versionEndType;

        public NvdRequestBuilder withCveId(String cveId) {
            this.cveId = cveId;
            parameterBuilder.addParameter(NvdConstants.CVE_ID, cveId);
            return this;
        }

        public NvdRequestBuilder withApiKey(String apiKey) {
            this.apiKey= apiKey;
            return this;
        }

        public NvdRequestBuilder withStartIndex(String startIndex) {
            this.startIndex = startIndex;
            return this;
        }

        public NvdRequestBuilder withResultsPerPage(String resultsPerPage) {
            this.resultsPerPage = resultsPerPage;
            parameterBuilder.addParameter(NvdConstants.RESULTS_PER_PAGE, resultsPerPage);
            return this;
        }

        public NvdRequestBuilder withLastModStartEndDates(String lastModStartDate, String lastModEndDate) {
            this.lastModStartDate = lastModStartDate;
            this.lastModEndDate = lastModEndDate;
            parameterBuilder.addParameter(NvdConstants.LAST_MOD_START_DATE, lastModStartDate);
            parameterBuilder.addParameter(NvdConstants.LAST_MOD_END_DATE, lastModEndDate);
            return this;
        }

        public NvdRequestBuilder withKeyWordSearch(String keywordSearch) {
            this.keywordSearch = keywordSearch;
            parameterBuilder.addParameter(NvdConstants.KEYWORD_SEARCH, keywordSearch);
            return this;
        }

        public NvdRequestBuilder withKeywordExactMatch(String keywordExactMatch) {
            this.keywordExactMatch = keywordExactMatch;
            parameterBuilder.addParameter(NvdConstants.KEYWORD_EXACT_MATCH, keywordExactMatch);
            return this;
        }

        public NvdRequestBuilder withVirtualMatchString(String virtualMatchString) {
            this.virtualMatchString = virtualMatchString;
            parameterBuilder.addParameter(NvdConstants.VIRTUAL_MATCH_STRING, virtualMatchString);
            return this;
        }

        public NvdRequestBuilder withCpeName(String cpeName) {
            this.cpeName = cpeName;
            parameterBuilder.addParameter(NvdConstants.CPE_NAME, cpeName);
            return this;
        }

        public NvdRequestBuilder withCveTag(String cveTag) {
            this.cveTag = cveTag;
            parameterBuilder.addParameter(NvdConstants.CVE_TAG, cveTag);
            return this;
        }

        public NvdRequestBuilder withCvssV2Metrics(String cvssV2Metrics) {
            this.cvssV2Metrics = cvssV2Metrics;
            parameterBuilder.addParameter(NvdConstants.CVSS_V2_METRICS, cvssV2Metrics);
            return this;
        }

        public NvdRequestBuilder withCvssV2Severity(String cvssV2Severity) {
            this.cvssV2Severity = cvssV2Severity;
            parameterBuilder.addParameter(NvdConstants.CVSS_V2_SEVERITY, cvssV2Severity);
            return this;
        }

        public NvdRequestBuilder withCvssV3Metrics(String cvssV3Metrics) {
            this.cvssV3Metrics = cvssV3Metrics;
            parameterBuilder.addParameter(NvdConstants.CVSS_V3_METRICS, cvssV3Metrics);
            return this;
        }

        public NvdRequestBuilder withCvssV3Severity(String cvssV3Severity) {
            this.cvssV3Severity = cvssV3Severity;
            parameterBuilder.addParameter(NvdConstants.CVSS_V3_SEVERITY, cvssV3Severity);
            return this;
        }

        public NvdRequestBuilder withCvssV4Metrics(String cvssV4Metrics) {
            this.cvssV4Metrics = cvssV4Metrics;
            parameterBuilder.addParameter(NvdConstants.CVSS_V4_METRICS, cvssV4Metrics);
            return this;
        }

        public NvdRequestBuilder withCvssV4Severity(String cvssV4Severity) {
            this.cvssV4Severity = cvssV4Severity;
            parameterBuilder.addParameter(NvdConstants.CVSS_V4_SEVERITY, cvssV4Severity);
            return this;
        }

        public NvdRequestBuilder withCweId(String cweId) {
            this.cweId = cweId;
            parameterBuilder.addParameter(NvdConstants.CWE_ID, cweId);
            return this;
        }

        public NvdRequestBuilder withHasCertAlerts(String hasCertAlerts) {
            this.hasCertAlerts = hasCertAlerts;
            parameterBuilder.addParameter(NvdConstants.HAS_CERT_ALERTS, hasCertAlerts);
            return this;
        }

        public NvdRequestBuilder withHasCertNotes(String hasCertNotes) {
            this.hasCertNotes = hasCertNotes;
            parameterBuilder.addParameter(NvdConstants.HAS_CERT_NOTES, hasCertNotes);
            return this;
        }

        public NvdRequestBuilder withHasKev(String hasKev) {
            this.hasKev = hasKev;
            parameterBuilder.addParameter(NvdConstants.HAS_KEV, hasKev);
            return this;
        }

        public NvdRequestBuilder withHasOval(String hasOval) {
            this.hasOval = hasOval;
            parameterBuilder.addParameter(NvdConstants.HAS_OVAL, hasOval);
            return this;
        }

        public NvdRequestBuilder withIsVulnerable(String isVulnerable) {
            this.isVulnerable = isVulnerable;
            parameterBuilder.addParameter(NvdConstants.IS_VULNERABLE, isVulnerable);
            return this;
        }

        public NvdRequestBuilder withNoRejected(String noRejected) {
            this.noRejected = noRejected;
            parameterBuilder.addParameter(NvdConstants.NO_REJECTED, noRejected);
            return this;
        }

        public NvdRequestBuilder withPubStartEndDates(String pubStartDate, String pubEndDate) {
            this.pubStartDate = pubStartDate;
            this.pubEndDate = pubEndDate;
            parameterBuilder.addParameter(NvdConstants.PUB_START_DATE, pubStartDate);
            parameterBuilder.addParameter(NvdConstants.PUB_END_DATE, pubEndDate);
            return this;
        }

        public NvdRequestBuilder withSourceIdentifier(String sourceIdentifier) {
            this.sourceIdentifier = sourceIdentifier;
            parameterBuilder.addParameter(NvdConstants.SOURCE_IDENTIFIER, sourceIdentifier);
            return this;
        }

        public NvdRequestBuilder withVersionEnd(String versionEnd) {
            this.versionEnd = versionEnd;
            parameterBuilder.addParameter(NvdConstants.VERSION_END, versionEnd);
            return this;
        }

        public NvdRequestBuilder withVersionEndType(String versionEndType) {
            this.versionEndType = versionEndType;
            parameterBuilder.addParameter(NvdConstants.VERSION_END_TYPE, versionEndType);
            return this;
        }

        public NvdRequestBuilder defaultMirrorBuild(String startIndex) {
            this.httpMethod = HTTPMethod.GET;
            this.baseUri = Constants.NVD_CVE_URI;
            this.apiKey = System.getenv("NVD_KEY");
            this.startIndex = startIndex;
            this.resultsPerPage = Integer.toString(Constants.NVD_MAX_PAGE_SIZE);

            parameterBuilder.addParameter(NvdConstants.RESULTS_PER_PAGE, Integer.toString(Constants.NVD_MAX_PAGE_SIZE));
            parameterBuilder.addParameter(NvdConstants.VERSION_END_TYPE, versionEndType);

            return this;
        }

        public NvdRequest build() {
            this.params = parameterBuilder.build();
            return new NvdRequest(this);
        }
    }


    // if using this class as a template to extend Request functionality, overloaded constructors providing
    // options for POST/PUT etc. requests could go here. However, for the NVD, GET is likely all that will be offered.

    /**
     * Executes the API request and handles the result
     * @return the requested NvdResponse object
     */
    @Override
    public NvdResponse executeRequest() {
        // the NVD only offers HTTP GET endpoints so this class only implements executeGetRequest()
        // For different extensions of the Request superclass you might have a switch statement based on the
        // httpMethod param along with other private execute methods. e.g. executePostRequest(), executeDeleteRequest() etc.
        return executeGetRequest();
    }


    private NvdResponse executeGetRequest() {
        HttpGet request = new HttpGet();
        URI uri = buildUri();
        request.setURI(uri);
        request.setHeaders(headers);

        return makeHttpCall(request);
    }

    private URI buildUri() {
        try {
            return new URIBuilder(baseUri).addParameters(params).build();
        } catch (URISyntaxException e) {
            LOGGER.error(Constants.URI_BUILD_FAILURE_MESSAGE, e);
            throw new RuntimeException(e);
        }
    }

    private NvdResponse makeHttpCall(HttpGet request) {
        NvdResponse nvdResponse = new NvdResponse();

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(request)) {

            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                String json = new JsonResponseHandler().handleResponse(response);
                nvdResponse.setCveResponse(new NvdCveMarshaller().unmarshalJson(json));
                nvdResponse.setStatus(status);
            } else {
                LOGGER.info(Constants.RESPONSE_STATUS_MESSAGE, status);
                throw new IOException(Constants.REQUEST_EXECUTION_FAILURE_MESSAGE + response.getStatusLine());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return nvdResponse;
    }
}

