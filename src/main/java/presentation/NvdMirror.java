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
    private static final MirrorService mirrorService = new MirrorService();

    public static void buildNvdMirror(String dbContext) {
        nvdApiService.handleGetPaginatedCves(dbContext, Constants.DEFAULT_START_INDEX, Constants.NVD_MAX_PAGE_SIZE);
    }

    public static void updateNvdMirror(String dbContext) throws DataAccessException {
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
}