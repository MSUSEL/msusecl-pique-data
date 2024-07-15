package persistence;

/**
 * I'm not really sure that this is going to work, but it's a good experiment
 */
public interface ISQLQueryService {
    String buildSelectQuery(String[] columns, String[] tables, String[] conditions);

    String buildInsertQuery(String[] tables, String[] values, String type);

    String buildUpdateQuery(String[] tables, String[] values);

    String buildDeleteQuery(String[] keys, String[] tables, String[] conditions);

    String getStoredQuery(CveStoredQueryKeys key, String queryId);
}
