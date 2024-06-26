package database.postgreSQL;

import api.cveData.CVEResponse;
import api.cveData.Cve;
import database.IMetaDataDao;
import api.cveData.NvdMirrorMetaData;

import java.util.List;

public class PostgresMetaDataDao implements IMetaDataDao<NvdMirrorMetaData> {

    @Override
    public void updateMetaData(NvdMirrorMetaData metaData) {

    }

    @Override
    public NvdMirrorMetaData retrieveMetaData() {
        return null;
    }
}
