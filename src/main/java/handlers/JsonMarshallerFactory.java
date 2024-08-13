package handlers;

import businessObjects.cve.Cve;
import businessObjects.cve.CveEntity;
import businessObjects.ghsa.SecurityAdvisory;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonMarshallerFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonMarshallerFactory.class);

    public IJsonMarshaller<CveEntity> getCveEntityMarshaller() {
        return new IJsonMarshaller<CveEntity>() {
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
            public String marshalJson(CveEntity entity) {
                return new Gson().toJson(entity);
            }
        };
    }

    public IJsonMarshaller<Cve> getCveMarshaller() {
        return new IJsonMarshaller<Cve>() {
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
            public String marshalJson(Cve entity) {
                return new Gson().toJson(entity);
            }
        };
    }

    public IJsonMarshaller<SecurityAdvisory> getSecurityAdvisoryMarshaller() {
        return new SecurityAdvisoryMarshaller();
    }

}
