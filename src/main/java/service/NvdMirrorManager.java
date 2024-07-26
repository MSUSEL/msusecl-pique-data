package service;

import businessObjects.NvdRequestBuilder;
import businessObjects.cve.CVEResponse;
import businessObjects.cve.Cve;
import businessObjects.cve.NvdMirrorMetaData;
import common.Constants;
import exceptions.ApiCallException;
import exceptions.DataAccessException;
import handlers.NvdCveMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.IBulkDao;
import persistence.IMetaDataDao;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class NvdMirrorManager {
    private final NvdApiService nvdApiService = new NvdApiService();
    private final CveResponseProcessor cveResponseProcessor = new CveResponseProcessor();
    private final DbContextResolver dbContextResolver = new DbContextResolver();
    private static final Logger LOGGER = LoggerFactory.getLogger(NvdMirrorManager.class);
    private final NvdCveMarshaller nvdCveMarshaller = new NvdCveMarshaller();

    /**
     * Gets CVEs in bulk from the NVD and stores them in the given database context.
     *
     * @param dbContext defines database context (currently SECL's postgres NVD Mirror)
     */
    public void handleBuildMirror(String dbContext) throws DataAccessException, ApiCallException {
        int cveCount = 1;

        for (int i = Constants.DEFAULT_START_INDEX; i < cveCount; i += Constants.NVD_MAX_PAGE_SIZE) {
            CVEResponse response = nvdApiService.performApiCall(
                    new NvdRequestBuilder().withFullMirrorDefaults(Integer.toString(i)).build());
            cveCount = resetCveCount(cveCount, response);
            persistPaginatedData(dbContext, response, i, cveCount);
            handleSleep(i, cveCount);   // avoids hitting NVD rate limits
        }
    }

    /**
     * Handles updating the SECL NVD Mirror
     * @param dbContext defines database context (currently SECL's postgres
     *                  NVD Mirror or local containerized mongodb instance)
     * @param lastModStartDate Timestamp of previous call to NVD CVE API to update SECL mirror
     * @param lastModEndDate Typically it is the current time - but provides the upper bound to the time window
     *                       from which to pull updates
     */
    public void handleUpdateNvdMirror(String dbContext, String lastModStartDate, String lastModEndDate) throws DataAccessException, ApiCallException {
        CVEResponse response = nvdApiService.performApiCall(
                new NvdRequestBuilder()
                        .withApiKey(Constants.NVD_API_KEY)
                        .withLastModStartEndDates(lastModStartDate, lastModEndDate)
                        .build());
        persistMetadata(dbContext, response);
        persistCveDetails(dbContext, response);
    }

    /**
     * Handles building a full or partial NVD mirror from a json file.
     * The file must be structured in exactly the same format as a CveResponse
     * @param dbContext defines database context (currently SECL's postgres
     *                  NVD Mirror or local containerized mongodb instance)
     * @param filepath Path to the json file formatted as a CveResponse
     * @throws DataAccessException
     */
    public void handleBuildMirrorFromJsonFile(String dbContext, Path filepath) throws DataAccessException {
        CVEResponse fileContents = processFile(filepath);
        persistMetadata(dbContext, fileContents);
        persistCveDetails(dbContext, fileContents);
    }

    private int resetCveCount(int cveCount, CVEResponse response) {
        return cveCount == 1
                ? cveResponseProcessor.extractTotalResults(response)
                : cveCount;
    }

    private void persistPaginatedData(String dbContext, CVEResponse response, int loopIndex, int cveCount) throws DataAccessException {
        persistCveDetails(dbContext, response);
        if(loopIndex == cveCount - 1) {
            persistMetadata(dbContext, response);
        }
    }

    private void persistCveDetails(String dbContext, CVEResponse response) throws DataAccessException {
        IBulkDao<Cve> dao = dbContextResolver.resolveBulkDao(dbContext);
        dao.insertMany(cveResponseProcessor.extractAllCves(response));
    }

    private void persistMetadata(String dbContext, CVEResponse response) throws DataAccessException {
        IMetaDataDao<NvdMirrorMetaData> dao = dbContextResolver.resolveMetaDataDao(dbContext);
        dao.updateMetaData(cveResponseProcessor.formatNvdMetaData(response));
    }

    private void handleSleep(int startIndex, int cveCount) {
        try {
            if (startIndex != cveCount - 1) {
                Thread.sleep(Constants.DEFAULT_NVD_REQUEST_SLEEP);
            }
        } catch (InterruptedException e) {
            LOGGER.error("Thread interrupted", e);
            throw new RuntimeException(e);
        }
    }

    private CVEResponse processFile(Path filepath) {
        String json = readJsonFile(filepath);
        return nvdCveMarshaller.unmarshalJson(json);
    }

    private String readJsonFile(Path filepath) {
        StringBuilder builder = new StringBuilder();
        try(Stream<String> stream = Files.lines(filepath, StandardCharsets.UTF_8)) {
            stream.forEach(s -> builder.append(s).append("\n"));
            return builder.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
