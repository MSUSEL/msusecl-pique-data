package presentation;

import businessObjects.cve.Cve;
import businessObjects.cve.NvdMirrorMetaData;
import exceptions.ApiCallException;
import exceptions.DataAccessException;
import service.INvdMirrorService;
import service.MirrorService;
import service.NvdMirrorManager;

import java.nio.file.Path;
import java.time.Instant;

/**
 * The NvdMirror class is used to easily create and update mirrors of the National Vulnerability Database.
 */
public final class NvdMirror {
    private final INvdMirrorService mirrorService;
    private final NvdMirrorManager nvdMirrorManager;

    public NvdMirror(INvdMirrorService mirrorService, NvdMirrorManager nvdMirrorManager) {
        this.mirrorService = mirrorService;
        this.nvdMirrorManager = nvdMirrorManager;
    }

    public void buildNvdMirror(String dbContext) throws DataAccessException, ApiCallException {
        nvdMirrorManager.handleBuildMirror(dbContext);
    }

    public void updateNvdMirror(String dbContext) throws DataAccessException, ApiCallException {
        nvdMirrorManager.handleUpdateNvdMirror(
                dbContext,
                mirrorService.handleGetCurrentMetaData(dbContext).getTimestamp(),
                Instant.now().toString());
    }

    public NvdMirrorMetaData getMetaData(String dbContext) throws DataAccessException {
        return mirrorService.handleGetCurrentMetaData(dbContext);
    }

    public void insertSingleCve(String dbContext, Cve cve) throws DataAccessException {
        mirrorService.handleInsertSingleCve(dbContext, cve);
    }

    public void deleteSingleCve(String dbContext, String cveId) throws DataAccessException {
        mirrorService.handleDeleteSingleCve(dbContext, cveId);
    }

    public void buildMirrorFromJsonFile(String dbContext, Path filepath) throws DataAccessException {
        nvdMirrorManager.handleBuildMirrorFromJsonFile(dbContext, filepath);
    }
}