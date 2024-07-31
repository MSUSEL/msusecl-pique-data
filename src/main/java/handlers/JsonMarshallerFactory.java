package handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonMarshallerFactory {
    private final Class<?> type;
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonMarshallerFactory.class);

    public JsonMarshallerFactory(Class<?> type) {
        this.type = type;
    }

    public IJsonMarshaller getMarshaller() {
        return new IJsonMarshaller() {
            @Override
            public Object unmarshalJson(String json) {
                try {
                    return new Gson().fromJson(json, type);
                } catch (JsonSyntaxException e) {
                    LOGGER.error(Constants.MALFORMED_JSON_SYNTAX_MESSAGE, e);
                    throw new RuntimeException(e);
                }
            }

            @Override
            public String marshalJson(Object obj) {
                return new Gson().toJson(obj);
            }
        };
    }

}
