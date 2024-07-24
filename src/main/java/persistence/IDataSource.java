package persistence;

public interface IDataSource<T> {
    T getConnection();
}
