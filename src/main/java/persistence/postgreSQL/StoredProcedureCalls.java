package persistence.postgreSQL;

public final class StoredProcedureCalls {
    public static final String INSERT_BULK_CVES = "{call insert_cve_batch(?, ?)}";
}
