package persistence.postgreSQL;

public final class StoredProcedureCalls {
    public static final String UPSERT_BULK_CVES = "CALL nvd.upsert_cve_batch(?, ?)";
    public static final String UPSERT_CVE = "CALL nvd.upsert_cve_details(?, ?)";
    public static final String FETCH_CVE = "CALL nvd.fetch_cve(?)";
    public static final String FETCH_CVES = "CALL nvd.fetch_cves(?)";
    public static final String UPSERT_METADATA = "CALL nvd.upsert_metadata(?, ?, ?, ?)";
    public static final String FETCH_METADATA = "CALL nvd.fetch_metadata()";
    public static final String DELETE_CVE = "CALL nvd.delete_cve(?, ?, ?)";
}

