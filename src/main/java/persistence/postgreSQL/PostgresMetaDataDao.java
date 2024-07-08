package persistence.postgreSQL;

import persistence.IMetaDataDao;
import businessObjects.cve.NvdMirrorMetaData;

public final class PostgresMetaDataDao implements IMetaDataDao<NvdMirrorMetaData> {

    @Override
    public void updateMetaData(NvdMirrorMetaData metaData) {

    }

    @Override
    public NvdMirrorMetaData fetchMetaData() {
        return null;
    }
}
