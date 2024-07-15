package persistence.postgreSQL;

import businessObjects.baseClasses.AbstractBaseEntity;
import exceptions.DataAccessException;
import handlers.IResultsProcessor;
import persistence.CveStoredQueryKeys;
import persistence.IDataSource;
import persistence.IExperimentalDao;
import persistence.ISQLQueryService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class PersistentDao<T extends AbstractBaseEntity> implements IExperimentalDao<T> {
    private final ISQLQueryService queryService;
    private final IDataSource dataSource;
    private final IResultsProcessor processor;

    public PersistentDao(IDataSource dataSource, ISQLQueryService queryService, IResultsProcessor<ResultSet, T> processor) {
        this.queryService = queryService;
        this.dataSource = dataSource;
        this.processor = processor;
    }

    @Override
    public T fetchById(String id) throws SQLException {
        String query = queryService.getStoredQuery(CveStoredQueryKeys.SELECT, id);
        Connection conn = dataSource.getConnection();
        PreparedStatement statement = conn.prepareStatement(query);
        ResultSet rs = statement.executeQuery();
        // return result
        T Resposne = processor.processResults(rs);
    }

    @Override
    public void insert(T t) throws DataAccessException {

    }

    @Override
    public void update(T t) throws DataAccessException {

    }

    @Override
    public void delete(String t) throws DataAccessException {

    }
}
