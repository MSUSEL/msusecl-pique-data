package persistence;

import java.util.List;

public interface IBulkDao<T> {
    void insertMany(List<T> entity);
    T[] fetchMany(String[] entities);
    T[] fetchAll();
}
