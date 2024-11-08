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

public class SecurityAdvisoryMarshaller{
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityAdvisoryMarshaller.class);

    public SecurityAdvisory unmarshalJson(String json) {
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

    public String marshalJson(SecurityAdvisory advisory) {
        return new Gson().toJson(advisory);
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
