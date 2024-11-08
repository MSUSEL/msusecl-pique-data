import org.junit.Test;
import persistence.IDataSource;
import persistence.postgreSQL.PgTableOperationsDao;
import persistence.postgreSQL.PostgresConnectionManager;
import service.CredentialService;

import java.sql.Connection;

import static common.Constants.CREDENTIALS_FILE_PATH;

public class PgTableOpsTest {
    private final IDataSource<Connection> conn = new PostgresConnectionManager(new CredentialService(CREDENTIALS_FILE_PATH));
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
