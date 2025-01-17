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
package common;

/**
 * These are exact terms used by the National Vulnerability Database CVE2.0 API
 * They are header and parameter names. The use of these constants is
 * encouraged in creating api calls to prevent typos
 */
public class NvdConstants {
    public static final String API_KEY = "apiKey";
    public static final String CPE_NAME = "cpeName";
    public static final String CVE_ID = "cveId";
    public static final String CVE_TAG = "cveTag";
    public static final String CVSS_V2_METRICS = "cvssV2Metrics";
    public static final String CVSS_V2_SEVERITY = "cvssV2Severity";
    public static final String CVSS_V3_METRICS = "cvssV3Metrics";
    public static final String CVSS_V3_SEVERITY = "cvssV3Severity";
    public static final String CVSS_V4_METRICS = "cvssV4Metrics";
    public static final String CVSS_V4_SEVERITY = "cvssV4Severity";
    public static final String CWE_ID = "cweId";
    public static final String HAS_CERT_ALERTS = "withHasCertAlerts";
    public static final String HAS_CERT_NOTES = "hasCertNotes";
    public static final String HAS_KEV = "hasKev";
    public static final String HAS_OVAL = "hasOval";
    public static final String IS_VULNERABLE = "IS_VULNERABLE";
    public static final String START_INDEX = "startIndex";
    public static final String RESULTS_PER_PAGE = "resultsPerPage";
    public static final String LAST_MOD_START_DATE = "lastModStartDate";
    public static final String LAST_MOD_END_DATE = "lastModEndDate";
    public static final String KEYWORD_SEARCH = "keywordSearch";
    public static final String KEYWORD_EXACT_MATCH = "keywordExactMatch";
    public static final String VIRTUAL_MATCH_STRING = "virtualMatchString";
    public static final String NO_REJECTED = "noRejected";
    public static final String PUB_START_DATE = "pubStartDate";
    public static final String PUB_END_DATE = "pubEndDate";
    public static final String SOURCE_IDENTIFIER = "sourceIdentifier";
    public static final String VERSION_END = "versionEnd";
    public static final String VERSION_END_TYPE = "versionEndType";
}
