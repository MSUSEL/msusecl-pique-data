package presentation;

import businessObjects.cve.Cve;
import businessObjects.cve.NvdMirrorMetaData;
import exceptions.ApiCallException;
import exceptions.DataAccessException;
import service.NvdApiService;
import service.MirrorService;
import service.NvdMirrorManager;

import java.nio.file.Path;
import java.time.Instant;

/**
 * The NvdMirror class is used to easily create and update mirrors of the National Vulnerability Database.
 */
public final class NvdMirror {
    private static final MirrorService mirrorService = new MirrorService();
    private static final NvdMirrorManager nvdMirrorManager = new NvdMirrorManager();

    public static void buildNvdMirror(String dbContext) throws DataAccessException, ApiCallException {
        nvdMirrorManager.handleBuildMirror(dbContext);
    }

    public static void updateNvdMirror(String dbContext) throws DataAccessException, ApiCallException {
        nvdMirrorManager.handleUpdateNvdMirror(
                dbContext,
                mirrorService.handleGetCurrentMetaData(dbContext).getTimestamp(),
                Instant.now().toString());
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

    public static void buildMirrorFromJsonFile(String dbContext, Path filepath) throws DataAccessException {
        nvdMirrorManager.handleBuildMirrorFromJsonFile(dbContext, filepath);
    }
}