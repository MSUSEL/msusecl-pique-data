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
package service;

import businessObjects.cve.NvdMirrorMetaData;
import handlers.INvdSerializer;
import persistence.IDataSource;
import persistence.IMetaDataDao;
import presentation.NvdRequestBuilder;
import businessObjects.cve.CveEntity;
import businessObjects.cve.Cve;
import exceptions.ApiCallException;
import exceptions.DataAccessException;
import org.apache.http.client.ResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.IDao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;

import static common.Constants.*;

public class NvdMirrorManager {
    private final IResponseProcessor cveResponseProcessor;
    private final ResponseHandler<String> jsonResponseHandler;
    private final INvdSerializer jsonSerializer;
    private final IDao<Cve> cveDao;
    private final IMetaDataDao<NvdMirrorMetaData> metadataDao;
    private final IDataSource<Connection> dataSource;
    private static final Logger LOGGER = LoggerFactory.getLogger(NvdMirrorManager.class);

    private final String SQL = "sql";
    private final String PLPGSQL = "plpgsql";

    public NvdMirrorManager(IResponseProcessor cveResponseProcessor,
                            ResponseHandler<String> jsonResponseHandler,
                            INvdSerializer jsonSerializer,
                            IDao<Cve> cveDao,
                            IMetaDataDao<NvdMirrorMetaData> metadataDao,
                            IDataSource<Connection> dataSource) {
        this.cveResponseProcessor = cveResponseProcessor;
        this.jsonResponseHandler = jsonResponseHandler;
        this.jsonSerializer = jsonSerializer;
        this.cveDao = cveDao;
        this.metadataDao = metadataDao;
        this.dataSource = dataSource;
    }

    /**
     * Initializes an NVD Mirror on an emtpy postgres instance
     * Creates schema, procedures, and functions
     * Builds necessary tables
     */
    public void handleInitializeMirror() {
        executeScript(MIGRATION_SCRIPT_PATH, SQL);
        executeScript(PG_STORED_PROCEDURES_PATH, PLPGSQL);
        hydrate();
    }

    /**
     * Gets CVEs in bulk from the NVD and stores them in the initialized mirror
     */
    public void handleBuildMirror() throws DataAccessException, ApiCallException {
        int cveCount = 1;

        for (int i = DEFAULT_START_INDEX; i < cveCount; i += NVD_MAX_PAGE_SIZE) {
            CveEntity response = new NvdRequestBuilder(jsonResponseHandler, jsonSerializer)
                    .withFullMirrorDefaults(Integer.toString(i))
                    .build()
                    .executeRequest().getEntity();
            cveCount = resetCveCount(cveCount, response);
            persistPaginatedData(response, i, cveCount);
            handleSleep(i, cveCount);   // avoids hitting NVD rate limits
        }
    }

    /**
     * Handles updating an NVD Mirror
     * @param lastModStartDate Timestamp of previous call to NVD CVE API to update mirror
     * @param lastModEndDate Typically it is the current time - provides the upper bound to the time window
     *                       from which to pull updates
     */
    public void handleUpdateNvdMirror(String lastModStartDate, String lastModEndDate) throws DataAccessException, ApiCallException {
        CveEntity response = new NvdRequestBuilder(jsonResponseHandler, jsonSerializer)
                        .withApiKey(NVD_API_KEY)
                        .withLastModStartEndDates(lastModStartDate, lastModEndDate)
                        .build()
                .executeRequest().getEntity();

        persistMetadata(response);
        persistCveDetails(response);
    }


    private void executeScript(String filepath, String scriptType) {
        String line;
        StringBuilder query = new StringBuilder();

        String lineEnd = determineLineEnd(scriptType);

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
        if (scriptType.equals(SQL)) {
            lineEnd = ";";
        } else if (scriptType.equals(PLPGSQL)) {
            lineEnd = "$$;";
        } else {
            throw new DataAccessException("Incorrect database script type");
        }
        return lineEnd;
    }

    private void executeQuery(String query) throws DataAccessException {
        Connection conn = dataSource.getConnection();

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            int rowsAffected = statement.executeUpdate();
            System.out.printf("Query: %s\n", query);
            System.out.printf("Rows Affected: %s\n\n", rowsAffected);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    private void hydrate() {
        NvdMirrorMetaData metadata = metadataDao.fetch();
        if (metadata.getLastTimestamp() == null) {
            handleBuildMirror();
        } else {
            handleUpdateNvdMirror(metadata.getLastTimestamp(), Instant.now().toString());
        }
    }

    private int resetCveCount(int cveCount, CveEntity response) {
        return cveCount == 1
                ? cveResponseProcessor.extractTotalResults(response)
                : cveCount;
    }

    private void persistPaginatedData(CveEntity response, int loopIndex, int cveCount) throws DataAccessException {
        persistCveDetails(response);
        if (loopIndex >= cveCount - NVD_MAX_PAGE_SIZE) {
            persistMetadata(response);
        }
    }

    private void persistCveDetails(CveEntity response) throws DataAccessException {
        cveDao.upsert(cveResponseProcessor.extractAllCves(response));
    }

    private void persistMetadata(CveEntity response) throws DataAccessException {
        metadataDao.upsert(cveResponseProcessor.extractFormattedNvdMetaData(response));
    }

    private void handleSleep(int startIndex, int cveCount) {
        try {
            if (startIndex != cveCount - 1) {
                Thread.sleep(DEFAULT_NVD_REQUEST_SLEEP);
            }
        } catch (InterruptedException e) {
            LOGGER.error("Thread interrupted", e);
            throw new RuntimeException(e);
        }
    }
}
