package persistence.postgreSQL;

public final class StoredProcedureCalls {
    public static final String UPSERT_BATCH_VULNERABILITIES = "CALL nvd.upsert_batch_vulnerabilities(?, ?)";
    public static final String UPSERT_VULNERABILITY = "CALL nvd.upsert_vulnerability(?, ?)";
    public static final String UPSERT_METADATA = "CALL nvd.upsert_metadata(?, ?, ?, ?)";
    public static final String DELETE_CVE = "CALL nvd.delete_cve(?, ?, ?)";
}

