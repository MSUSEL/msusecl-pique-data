package presentation;

import businessObjects.cve.Cve;
import businessObjects.cve.NvdMirrorMetaData;
import common.Constants;
import exceptions.DataAccessException;
import service.NvdApiService;
import service.MirrorService;

import java.time.Instant;

public class NvdMirror {
    private static final NvdApiService nvdApiService =  new NvdApiService();
    private static final MirrorService MIRROR_SERVICE = new MirrorService();

    public static void buildNvdMirror(String dbContext) {
        nvdApiService.handleGetPaginatedCves(dbContext, Constants.DEFAULT_START_INDEX, Constants.NVD_MAX_PAGE_SIZE);
    }

    public static void updateNvdMirror(String dbContext) throws DataAccessException {
        NvdMirrorMetaData metadata = MIRROR_SERVICE.handleGetCurrentMetaData(dbContext);
        Instant instant = Instant.now();
        nvdApiService.handleUpdateNvdMirror(dbContext, metadata.getTimestamp(), instant.toString());
    }

    public static NvdMirrorMetaData getMetaData(String dbContext) throws DataAccessException {
        return MIRROR_SERVICE.handleGetCurrentMetaData(dbContext);
    }

    public static void insertSingleCve(String dbContext, Cve cve) throws DataAccessException {
        MIRROR_SERVICE.handleInsertSingleCve(dbContext, cve);
    }
}