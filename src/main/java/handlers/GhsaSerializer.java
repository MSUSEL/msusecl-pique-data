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
package handlers;

import businessObjects.ghsa.Cwes;
import businessObjects.ghsa.Nodes;
import businessObjects.ghsa.SecurityAdvisory;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class GhsaSerializer implements IGhsaSerializer<SecurityAdvisory> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GhsaSerializer.class);

    @Override
    public SecurityAdvisory deserialize(String json) {
        SecurityAdvisory securityAdvisory = new SecurityAdvisory();
        Cwes cwes = new Cwes();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject jsonResponse = jsonObject.optJSONObject("data").optJSONObject("securityAdvisory");
            if (jsonResponse != null) {
                securityAdvisory.setGhsaId(jsonResponse.optString("ghsaId"));
                securityAdvisory.setSummary(jsonResponse.optString("summary"));
                cwes.setNodes(getNodesFromJson(jsonResponse));
                securityAdvisory.setCwes(cwes);
            } else {
                LOGGER.info("GHSA response was null");
            }
        } catch (JSONException e) {
            LOGGER.error("Malformed Json", e);
            throw new RuntimeException(e);
        }

        return securityAdvisory;
    }

    @Override
    public String serialize(SecurityAdvisory ghsaSubgraph) {
        return new Gson().toJson(ghsaSubgraph);
    }

    private List<Nodes> getNodesFromJson(JSONObject response) {
        ArrayList<Nodes> nodes = new ArrayList<>();
        try {
            JSONArray jsonNodes = response.optJSONObject("cwes").optJSONArray("nodes");
            for(int i = 0; i < jsonNodes.length(); i++) {
                Nodes cweNode = new Nodes();
                cweNode.setCweId(jsonNodes.optJSONObject(i).getString("cweId"));
                nodes.add(cweNode);
            }
        } catch (JSONException e) {
            LOGGER.error("Malformed Json", e);
            throw new RuntimeException(e);
        }
        return nodes;
    }
}
