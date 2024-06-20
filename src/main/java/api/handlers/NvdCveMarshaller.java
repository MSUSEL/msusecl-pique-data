package api.handlers;

import api.cveData.CVEResponse;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NvdCveMarshaller implements IJsonMarshaller<CVEResponse> {
    private static final Logger LOGGER = LoggerFactory.getLogger(NvdCveMarshaller.class);

    @Override
    public CVEResponse unmarshalJson(String json) {
        try {
            return new Gson().fromJson(json, CVEResponse.class);
        } catch (JsonSyntaxException e) {
            LOGGER.error("Incorrect JSON syntax - unable to parse to object", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String marshalJson(CVEResponse cveResponse) {
        String json = new Gson().toJson(cveResponse);
        System.out.println(json);
        return new Gson().toJson(cveResponse);
    }
}
