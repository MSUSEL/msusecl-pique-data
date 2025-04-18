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

import businessObjects.GHSAResponse;
import businessObjects.ghsa.Nodes;
import businessObjects.ghsa.SecurityAdvisory;

import java.util.ArrayList;
import java.util.List;

public final class GhsaResponseProcessor {
    // Methods to handle raw GHSA Response object
    public List<Nodes> extractCweNodes(GHSAResponse ghsaResponse) {
        return ghsaResponse.getEntity().getCwes().getNodes();
    }

    public String extractGhsaId(GHSAResponse ghsaResponse) {
        return ghsaResponse.getEntity().getGhsaId();
    }

    public String extractSummary(GHSAResponse ghsaResponse) {
        return ghsaResponse.getEntity().getSummary();
    }

    // methods to extract fields from Security Advisories
    public List<String> extractCweIds(SecurityAdvisory advisory) {
        List<Nodes> nodes = advisory.getCwes().getNodes();
        List<String> ids = new ArrayList<>();
        for (Nodes node : nodes) {
            ids.add(node.getCweId());
        }
        return ids;
    }
}
