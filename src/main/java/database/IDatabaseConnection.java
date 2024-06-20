package database;

public interface IDatabaseConnection<T> {
    T getConnection(String driver, String hostname, String port, String dbname, String username, String password);
}
