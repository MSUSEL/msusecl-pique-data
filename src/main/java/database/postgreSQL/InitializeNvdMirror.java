package database.postgreSQL;

import java.sql.Connection;
import java.sql.SQLException;

public class InitializeNvdMirror {

    public InitializeNvdMirror() {
        try {
            Connection connection = PostgresConnectionManager.getConnection();

        } catch (SQLException e) {
            // TODO fix error handling/logging here
            System.out.println(e.getStackTrace());
            throw new RuntimeException(e);
        }
    }
}
