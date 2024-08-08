import org.junit.Test;
import persistence.IDataSource;
import persistence.postgreSQL.PgTableOperationsDao;
import persistence.postgreSQL.PostgresConnectionManager;
import service.CredentialService;

import java.sql.Connection;

public class PgTableOpsTest {
    @Test
    public void rebuildNvdMirror() {
        IDataSource<Connection> conn = new PostgresConnectionManager(new CredentialService());
        PgTableOperationsDao dao = new PgTableOperationsDao(conn);
        dao.buildCveTable();
    }
}
