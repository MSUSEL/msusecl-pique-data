package handlers;

import businessObjects.cve.CveEntity;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO parameterize CVE marshalllers instead of implementing multiple classes
public class CveEntityMarshaller implements IJsonMarshaller<CveEntity> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CveMarshaller.class);

    @Override
    public CveEntity unmarshalJson(String json) {
        try {
            return new Gson().fromJson(json, CveEntity.class);
        } catch (JsonSyntaxException e) {
            LOGGER.error(Constants.MALFORMED_JSON_SYNTAX_MESSAGE, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String marshalJson(CveEntity cveEntity) {
        return new Gson().toJson(cveEntity);
    }
}
