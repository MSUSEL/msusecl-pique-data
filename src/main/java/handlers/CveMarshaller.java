package handlers;

import businessObjects.cveData.Cve;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CveDetailsMarshaller implements IJsonMarshaller<Cve> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CveDetailsMarshaller.class);

    @Override
    public Cve unmarshalJson(String json) {
        try {
            return new Gson().fromJson(json, Cve.class);
        } catch (JsonSyntaxException e) {
            LOGGER.error("Incorrect JSON syntax - unable to parse to object", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String marshalJson(Cve cveDetails) {
        return new Gson().toJson(cveDetails);
    }
}
