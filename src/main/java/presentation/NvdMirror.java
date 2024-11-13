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
import com.sun.jdi.Mirror;
import exceptions.ApiCallException;
import exceptions.DataAccessException;
import persistence.IDataSource;
import persistence.postgreSQL.Migration;
import service.INvdMirrorService;
import service.NvdMirrorManager;

import java.sql.Connection;
import java.time.Instant;

/**
 * The NvdMirror class is used to easily create and update mirrors of the National Vulnerability Database.
 */
public final class NvdMirror {
    private final INvdMirrorService mirrorService;
    private final NvdMirrorManager nvdMirrorManager;
    private final Migration migration;

    public NvdMirror(INvdMirrorService mirrorService, NvdMirrorManager nvdMirrorManager, Migration migration) {
        this.mirrorService = mirrorService;
        this.nvdMirrorManager = nvdMirrorManager;
        this.migration = migration;
    }

    /**
     * Builds an NVD Mirror from Scratch. This method requires and existing blank postgres database
     * It does NOT require existing tables or relations as it builds tables and creates procedures.
     * This works for both the persistent mirror at the SECL and local mirrors so double check that
     * you are using the correct credentials.
     * @throws DataAccessException
     * @throws ApiCallException
     */
    public void buildAndHydrateMirror() throws DataAccessException, ApiCallException {
        migration.migrate();
    }

    /**
     * hydrates existing, blank, tables in a postgres database with all data associated with an
     * NVD mirror.
     * @throws DataAccessException
     * @throws ApiCallException
     */
    public void buildNvdMirror() throws DataAccessException, ApiCallException {
        nvdMirrorManager.handleBuildMirror();
    }

    /**
     * Checks for any updates from the NVD CVE2.0 API that have occurred since the last build or
     * update of an NVD mirror.
     * @throws DataAccessException
     * @throws ApiCallException
     */
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