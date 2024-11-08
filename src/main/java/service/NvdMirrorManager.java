package service;

import handlers.IJsonSerializer;
import presentation.NvdRequestBuilder;
import businessObjects.cve.CveEntity;
import businessObjects.cve.Cve;
import businessObjects.cve.NvdMirrorMetaData;
import common.HelperFunctions;
import exceptions.ApiCallException;
import exceptions.DataAccessException;
import org.apache.http.client.ResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.IDao;

import java.nio.file.Path;
import java.util.Collections;

import static common.Constants.*;

public class NvdMirrorManager {
    private final CveResponseProcessor cveResponseProcessor;
    private final ResponseHandler<String> jsonResponseHandler;
    private final IJsonSerializer jsonSerializer;
    private final IDao<Cve> cveDao;
    private final IDao<NvdMirrorMetaData> metadataDao;
    private static final Logger LOGGER = LoggerFactory.getLogger(NvdMirrorManager.class);

    public NvdMirrorManager(CveResponseProcessor cveResponseProcessor,
                            ResponseHandler<String> jsonResponseHandler,
                            IJsonSerializer jsonSerializer,
                            IDao<Cve> cveDao,
                            IDao<NvdMirrorMetaData> metadataDao) {
        this.cveResponseProcessor = cveResponseProcessor;
        this.jsonResponseHandler = jsonResponseHandler;
        this.jsonSerializer = jsonSerializer;
        this.cveDao = cveDao;
        this.metadataDao = metadataDao;
    }

    /**
     * Gets CVEs in bulk from the NVD and stores them in the configured mirror
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

//    /**
//     * Handles building a full or partial NVD mirror from a json file.
//     * The file must be structured in exactly the same format as a CveResponse
//     *                  NVD Mirror or local containerized mongodb instance)
//     * @param filepath Path to the json file formatted as a CveResponse
//     * @throws DataAccessException
//     */
//    public void handleBuildMirrorFromJsonFile(Path filepath) throws DataAccessException {
//        CveEntity fileContents = processFile(filepath);
//        persistMetadata(fileContents);
//        persistCveDetails(fileContents);
//    }
//
//    public void handleDumpNvdToFile(String filepath) throws DataAccessException {
//            cveDao.dumpToFile(filepath);
//    }

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

    public void persistMetadata(CveEntity response) throws DataAccessException {
        metadataDao.upsert(Collections.singletonList(cveResponseProcessor.formatNvdMetaData(response)));
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

    private CveEntity processFile(Path filepath) {
        return jsonSerializer.deserialize(HelperFunctions.readJsonFile(filepath), CveEntity.class);
    }

}
