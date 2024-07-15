package persistence.postgreSQL;

import handlers.IJsonMarshaller;
import persistence.IDao;
import persistence.IDataSource;
import persistence.IMetaDataDao;
import businessObjects.cve.NvdMirrorMetaData;

import java.sql.Connection;

public final class PostgresMetaDataDao implements IMetaDataDao<NvdMirrorMetaData> {
    private final Connection conn;
    private final IJsonMarshaller<NvdMirrorMetaData> marshaller;

    public PostgresMetaDataDao(IDataSource<Connection> dataSource, IJsonMarshaller<NvdMirrorMetaData> marshaller) {
        this.conn = dataSource.getConnection();
        this.marshaller = marshaller;
    }

    @Override
    public void updateMetaData(NvdMirrorMetaData metaData) {
        //TODO implement this method
    }

    @Override
    public NvdMirrorMetaData fetchMetaData() {
        //TODO implement this method
        return null;
    }
}
