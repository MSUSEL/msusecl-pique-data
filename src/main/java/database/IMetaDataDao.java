package database;

import api.cveData.CVEResponse;

public interface IMetaDataDao<T> {
    void update(T metaData);

    void update(CVEResponse cveResponse);
}
