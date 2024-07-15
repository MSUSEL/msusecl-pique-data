package handlers;

import businessObjects.cve.Cve;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CveMarshaller implements IJsonMarshaller<Cve> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CveMarshaller.class);

    @Override
    public Cve unmarshalJson(String json) {
        try {
            return new Gson().fromJson(json, Cve.class);
        } catch (JsonSyntaxException e) {
            LOGGER.error(Constants.MALFORMED_JSON_SYNTAX_MESSAGE, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String marshalJson(Cve cveDetails) {
        return new Gson().toJson(cveDetails);
    }
}
