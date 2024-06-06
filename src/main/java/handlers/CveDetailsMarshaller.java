package handlers;

import api.cveData.CveDetails;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import database.interfaces.IJsonMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CveDetailsMarshaller implements IJsonMarshaller<CveDetails> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CveDetailsMarshaller.class);

    @Override
    public CveDetails unmarshalJson(String json) {
        try {
            return new Gson().fromJson(json, CveDetails.class);
        } catch (JsonSyntaxException e) {
            LOGGER.error("Incorrect JSON syntax - unable to parse to object", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String marshalJson(CveDetails cveDetails) {
        return new Gson().toJson(cveDetails);
    }
}
