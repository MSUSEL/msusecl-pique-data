package database.postgreSQL;

import database.IMetaDataDao;
import businessObjects.cveData.NvdMirrorMetaData;

public class PostgresMetaDataDao implements IMetaDataDao<NvdMirrorMetaData> {

    @Override
    public void updateMetaData(NvdMirrorMetaData metaData) {

    }

    @Override
    public NvdMirrorMetaData fetchMetaData() {
        return null;
    }
}
