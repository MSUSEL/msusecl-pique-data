package persistence.postgreSQL;

import persistence.CveStoredQueryKeys;
import persistence.ISQLQueryService;

public class PostgresQueryService implements ISQLQueryService {
    private final String SELECT_CVE_BY_ID = "SELECT details FROM nvd.cve WHERE cve_id = ?;";
    private final String INSERT_CVE_INTO_MIRROR = "INSERT INTO nvd.cve (cve_id, details) VALUES (?, CAST(? AS jsonb));";
    private final String DELETE_CVE_FROM_MIRROR = "DELETE FROM nvd.cve WHERE cve_id = ? RETURNING cve_id;";

    @Override
    public String buildSelectQuery(String[] columns, String[] tables, String[] conditions) {
        return "";
    }

    @Override
    public String buildInsertQuery(String[] tables, String[] values, String type) {
        return "";
    }

    @Override
    public String buildUpdateQuery(String[] tables, String[] values) {
        return "";
    }

    @Override
    public String buildDeleteQuery(String[] keys, String[] tables, String[] conditions) {
        return "";
    }

    @Override
    public String getStoredQuery(String queryId) {
        return "";
    }

    @Override
    public String getStoredQuery(CveStoredQueryKeys key, String queryId) {

        return resolveQuery(key);
    }

    private String resolveQuery(CveStoredQueryKeys key) {
        String query = "";

        switch (key) {
            case INSERT:
                query = INSERT_CVE_INTO_MIRROR;
                break;
            case SELECT:
                query = SELECT_CVE_BY_ID;
                break;
            case DELETE:
                query = DELETE_CVE_FROM_MIRROR;
                break;
        }

        return query;
    }
}
