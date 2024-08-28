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

    public void buildNvdMirror() throws DataAccessException, ApiCallException {
        nvdMirrorManager.handleBuildMirror();
    }

    public void updateNvdMirror() throws DataAccessException, ApiCallException {
        nvdMirrorManager.handleUpdateNvdMirror(
                mirrorService.handleGetCurrentMetaData().getTimestamp(),
                Instant.now().toString());
    }

    public NvdMirrorMetaData getMetaData() throws DataAccessException {
        return mirrorService.handleGetCurrentMetaData();
    }

    public void insertSingleCve(Cve cve) throws DataAccessException {
        mirrorService.handleInsertSingleCve(cve);
    }

    public void deleteSingleCve(String cveId) throws DataAccessException {
        mirrorService.handleDeleteSingleCve(cveId);
    }

//    public void buildMirrorFromJsonFile(Path filepath) throws DataAccessException {
//        nvdMirrorManager.handleBuildMirrorFromJsonFile(filepath);
//    }
}