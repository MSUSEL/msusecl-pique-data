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
package businessObjects.cve;

import businessObjects.baseClasses.BaseEntity;
import exceptions.ApiCallException;
import exceptions.DataAccessException;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static common.Constants.DB_QUERY_NO_RESULTS;

/**
 * This represents the root object for NVD response data.
 * This set of classes comprises the complete set of POJOs
 * necessary to deserialize the NVD API's json response.
 */
@Getter
@Setter
public final class CveEntity extends BaseEntity {
    private static final Logger LOGGER = LoggerFactory.getLogger(CveEntity.class);

    private int resultsPerPage;
    private int startIndex;
    private int totalResults;
    private String format;
    private String version;
    private String timestamp;
    private List<Vulnerability> vulnerabilities;

    public List<Vulnerability> getVulnerabilities() {
         return Optional.of(vulnerabilities)
                .filter(r -> !r.isEmpty())
                .orElseThrow(() -> {
                    LOGGER.info(DB_QUERY_NO_RESULTS);
                    return new ApiCallException(DB_QUERY_NO_RESULTS);
                });
    }
}
