package persistence.postgreSQL;

import businessObjects.cve.NvdMirrorMetaData;
import exceptions.DataAccessException;
import persistence.IDataSource;
import service.MirrorService;
import service.NvdMirrorManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.time.Instant;

import static common.Constants.*;

public class Migration {
    Connection conn;
    NvdMirrorManager manager;
    MirrorService mirrorService;

    public Migration(IDataSource<Connection> dataSource, NvdMirrorManager manager, MirrorService mirrorService) {
        this.conn = dataSource.getConnection();
        this.manager = manager;
        this.mirrorService = mirrorService;
    }

    public void migrate() {
        // Build/Update Database infrastructure;
        executeScript(MIGRATION_SCRIPT_PATH, "sql");
        executeScript(PG_STORED_PROCEDURES_PATH, "plpgsql");

        // Update Data
        hydrate();
    }

    private void executeScript(String filepath, String scriptType) {
        String line;
        String lineEnd = determineLineEnd(scriptType);
        StringBuilder query = new StringBuilder();


        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            while((line = reader.readLine()) != null) {
                query.append(line).append("\n");
                if (line.endsWith(lineEnd)) {
                    executeQuery(query.toString());
                    query.setLength(0);
                }
            }
        } catch (IOException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private String determineLineEnd(String scriptType) {
        String lineEnd;
        if (scriptType.equals("sql")) {
            lineEnd = ";";
        } else if (scriptType.equals("plpgsql")) {
            lineEnd = "$$;";
        } else {
            throw new DataAccessException("Incorrect database script type");
        }
        return lineEnd;
    }

    private void executeQuery(String query) throws DataAccessException {
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            int rowsAffected = statement.executeUpdate();
            System.out.printf("Query: %s%n", query);
            System.out.printf("Rows Affected: %s\n%n", rowsAffected);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    private void hydrate() {
        NvdMirrorMetaData metadata = mirrorService.handleGetCurrentMetaData();
        if (metadata.getTimestamp() == null) {
            manager.handleBuildMirror();
        } else {
            manager.handleUpdateNvdMirror(metadata.getTimestamp(), Instant.now().toString());
        }
    }
}
