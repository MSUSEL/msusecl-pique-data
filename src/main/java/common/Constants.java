package common;

/**
 * These are general constants used throughout the project
 */
public class Constants {
    public static final String DB_CONTEXT_LOCAL = "local";
    public static final String DB_CONTEXT_PERSISTENT = "persistent";
    public static final String DB_QUERY_FAILED = "Query Failed. ";
    public static final String DB_QUERY_NO_RESULTS = "Query returned no results. ";
    public static final int DEFAULT_NVD_REQUEST_SLEEP = 6000;
    public static final int DEFAULT_START_INDEX = 0;
    public static final String FAILED_TO_READ_FILE = "Failed to read file";
    public static final String GHSA_URI = "https://api.github.com/graphql";
    public static final String MALFORMED_JSON = "Malformed JSON";
    public static final String MONGO_NVD_METADATA_ID = "nvd_metadata";
    public static final String NVD_CVE_URI = "https://services.nvd.nist.gov/rest/json/cves/2.0";
    public static final int NVD_MAX_PAGE_SIZE = 2000;
    public static final String NVD_API_KEY_PATH = "nvd-api-key-path";
    public static final String GITHUB_TOKEN_PATH = "github-token-path";
}
