package database;

public interface IMetaDataDao<T> {
    void updateMetaData(T metaData);
    T fetchMetaData();
}
