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
package presentation;

import businessObjects.cve.Cve;
import businessObjects.cve.NvdMirrorMetaData;
import exceptions.ApiCallException;
import exceptions.DataAccessException;
import service.INvdMirrorService;
import service.NvdMirrorManager;

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
                mirrorService.handleGetCurrentMetaData().getLastTimestamp(),
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