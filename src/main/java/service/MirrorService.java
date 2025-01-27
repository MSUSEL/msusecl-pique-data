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
package service;

import businessObjects.cve.Cve;
import businessObjects.cve.Metrics;
import businessObjects.cve.NvdMirrorMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.IDao;
import exceptions.DataAccessException;
import persistence.postgreSQL.PostgresMetadataDao;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static common.Constants.DB_QUERY_NO_RESULTS;

public final class MirrorService implements INvdMirrorService{
    private final CveResponseProcessor cveResponseProcessor;
    private final IDao<Cve> cveDao;
    private final PostgresMetadataDao metadataDao;
    private static final Logger LOGGER = LoggerFactory.getLogger(MirrorService.class);

    public MirrorService(CveResponseProcessor cveResponseProcessor, IDao<Cve> cveDao, PostgresMetadataDao metadataDao) {
        this.cveResponseProcessor = cveResponseProcessor;
        this.cveDao = cveDao;
        this.metadataDao = metadataDao;
    }

    @Override
    public Cve handleGetCveById(String cveId) throws DataAccessException {
        return fetchOptionalResult(Collections.singletonList(cveId)).get(0);
    }

    @Override
    public List<Cve> handleGetCveById(List<String> cveIds) throws DataAccessException {
        return fetchOptionalResult(cveIds);

    }

    @Override
    public List<String> handleGetNvdCweName(String cveId) throws DataAccessException {
        return cveResponseProcessor.extractCweDescriptions(handleGetCveById(cveId));
    }

    @Override
    public NvdMirrorMetaData handleGetCurrentMetaData() throws DataAccessException {
        return metadataDao.fetch().get(0);
    }

    @Override
    public void handleInsertSingleCve(Cve cve) throws DataAccessException {
        cveDao.upsert(Collections.singletonList(cve));
    }

    @Override
    public void handleDeleteSingleCve(String cveId) throws DataAccessException {
        cveDao.delete(Collections.singletonList(cveId));
    }

    @Override
    public Map<String, Metrics> handleGetCvssMetrics(List<String> cveIds) throws DataAccessException {
        List<Cve> cves = handleGetCveById(cveIds);
        return cveResponseProcessor.extractCvssScores(cves);
    }

    private List<Cve> fetchOptionalResult(List<String> cveIds) {
        return Optional.of(cveDao.fetch(cveIds))
                .filter(r -> !r.isEmpty())
                .orElseThrow(() -> {
                    LOGGER.info(DB_QUERY_NO_RESULTS);
                    return new DataAccessException(DB_QUERY_NO_RESULTS);
                });
    }
}
