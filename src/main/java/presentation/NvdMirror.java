package presentation;

import businessObjects.cve.Cve;
import businessObjects.cve.NvdMirrorMetaData;
import common.Constants;
import exceptions.ApiCallException;
import exceptions.DataAccessException;
import service.NvdApiService;
import service.MirrorService;

import java.sql.SQLException;
import java.time.Instant;

/**
 * The NvdMirror class is used to manage the SECL mirror of the National Vulnerability Database.
 * In general, methods from this class are not for use in PIQUE wrappers. >1.0 releases of this
 * library will likely hide this class.
 */
public final class NvdMirror {
    private static final NvdApiService nvdApiService =  new NvdApiService();
    private static final MirrorService mirrorService = new MirrorService();

    public static void buildNvdMirror(String dbContext) throws DataAccessException {
        nvdApiService.handleGetPaginatedCves(dbContext, Constants.DEFAULT_START_INDEX, Constants.NVD_MAX_PAGE_SIZE);
    }

    public static void updateNvdMirror(String dbContext) throws DataAccessException, ApiCallException {
        NvdMirrorMetaData metadata = mirrorService.handleGetCurrentMetaData(dbContext);
        Instant instant = Instant.now();
        nvdApiService.handleUpdateNvdMirror(dbContext, metadata.getTimestamp(), instant.toString());
    }

    public static NvdMirrorMetaData getMetaData(String dbContext) throws DataAccessException {
        return mirrorService.handleGetCurrentMetaData(dbContext);
    }

    public static void insertSingleCve(String dbContext, Cve cve) throws DataAccessException {
        mirrorService.handleInsertSingleCve(dbContext, cve);
    }

    public static void deleteSingleCve(String dbContext, String cveId) throws DataAccessException {
        mirrorService.handleDeleteSingleCve(dbContext, cveId);
    }

    public static void insertMetaData(String dbContext) {
        mirrorService.handleInsertMetaData(dbContext);
    }
}