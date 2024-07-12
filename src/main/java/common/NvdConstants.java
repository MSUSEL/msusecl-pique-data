package common;

/**
 * These are exact terms used by the National Vulnerability Database
 * They are mostly header and parameter names
 * The use of these constants is encouraged in creating api calls to
 * prevent typos and for a very slight improvement in performance
 */
public class NvdConstants {
    public static final String CVE_ID = "cveId";
    public static final String API_KEY = "apiKey";
    public static final String START_INDEX = "startIndex";
    public static final String RESULTS_PER_PAGE = "resultsPerPage";
    public static final String LAST_MOD_START_DATE = "lastModStartDate";
    public static final String LAST_MOD_END_DATE = "lastModEndDate";
    public static final String KEYWORD_SEARCH = "keywordSearch";
    public static final String KEYWORD_EXACT_MATCH = "keywordExactMatch";
    public static final String VIRTUAL_MATCH_STRING = "virtualMatchString";
}
