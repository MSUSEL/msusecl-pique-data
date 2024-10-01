package common;

/**
 * These are general constants used throughout the project
 */
public final class Constants {
    public static final String DB_QUERY_FAILED = "Query Failed. ";
    public static final String DB_QUERY_NO_RESULTS = "Query returned no results. ";
    public static final int DEFAULT_NVD_REQUEST_SLEEP = 0;
    public static final int DEFAULT_START_INDEX = 0;
    public static final String GHSA_URI = "https://api.github.com/graphql";
    public static final String NVD_CVE_URI = "https://services.nvd.nist.gov/rest/json/cves/2.0";
    public static final int NVD_MAX_PAGE_SIZE = 2000;
    public static final String URI_BUILD_FAILURE_MESSAGE = "Could not build URI with given inputs";
    public static final String RESPONSE_STATUS_MESSAGE = "Response Status: {}";
    public static final String REQUEST_EXECUTION_FAILURE_MESSAGE = "Failed to execute request: ";
    public static final String MALFORMED_JSON_SYNTAX_MESSAGE = "Incorrect JSON syntax - uable to parse to object";
    public static final String NVD_API_KEY = System.getenv("NVD_KEY");
    public static final String CREDENTIALS_FILE_PATH = "./src/main/resources/credentials.json";
    public static final String MIGRATION_SCRIPT_PATH = "./src/main/resources/sql/BuildPostgres.sql";
    public static final String PG_STORED_PROCEDURES_PATH = "./src/main/resources/sql/StoredProcedures.sql";
}
