import org.junit.Test;
import persistence.IDataSource;
import persistence.postgreSQL.PgTableOperationsDao;
import persistence.postgreSQL.PostgresConnectionManager;
import service.CredentialService;

import java.sql.Connection;

public class PgTableOpsTest {
    private final IDataSource<Connection> conn = new PostgresConnectionManager(new CredentialService());
    PgTableOperationsDao dao = new PgTableOperationsDao(conn);

    @Test
    public void rebuildNvdMirror() {
        dao.buildCveTable();
    }

    @Test
    public void buildMetaDataTable() {
        dao.buildMetaDataTable();
    }
}
