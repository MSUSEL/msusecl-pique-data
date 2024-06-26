package presentation;

import common.Utils;
import service.NvdApiService;

public class PiqueNvdMirror {
    private static final NvdApiService nvdApiService =  new NvdApiService();

    public static void buildNvdMirror(String dbContext) {
        nvdApiService.handleGetPaginatedCves(dbContext, Utils.DEFAULT_START_INDEX, Utils.NVD_MAX_PAGE_SIZE);
    }
}
