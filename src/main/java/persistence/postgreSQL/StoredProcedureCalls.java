package persistence.postgreSQL;

public final class StoredProcedureCalls {
    public static final String UPSERT_BULK_CVES = "{call upsert_cve_batch(?, ?)}";
    public static final String UPSERT_CVE= "{call upsert_cve_details(?, ?)}";
    public static final String FETCH_CVE= "{call fetch_cve(?)}";
    public static final String FETCH_CVES= "{call fetch_cves(?)}";
    public static final String INSERT_METADATA = "{call insert_metadata(?, ?, ?, ?)}";
    public static final String FETCH_METADATA = "{call fetch_metadata()}";
    public static final String DELETE_CVE = "{call delete_cve(?, ?, ?)}";
}
