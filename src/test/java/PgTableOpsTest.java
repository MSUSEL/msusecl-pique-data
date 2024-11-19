/*
 * MIT License
 *
 * Copyright (c) 2024 Montana State University Software Engineering and Cybersecurity Laboratory
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import org.junit.Test;
import persistence.IDataSource;
import persistence.postgreSQL.PgTableOperationsDao;
import persistence.postgreSQL.PostgresConnectionManager;
import service.CredentialService;

import java.sql.Connection;

import static common.Constants.DEFAULT_CREDENTIALS_FILE_PATH;

public class PgTableOpsTest {
    private final IDataSource<Connection> conn = new PostgresConnectionManager(new CredentialService(DEFAULT_CREDENTIALS_FILE_PATH));
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
