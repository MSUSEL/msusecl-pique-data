package handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static common.Constants.MALFORMED_JSON_SYNTAX_MESSAGE;

public class JsonSerializer implements IJsonSerializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonSerializer.class);
    private final Gson gson;

    public JsonSerializer(Gson gson) {
        this.gson = gson;
    }

    public <T> String serialize(T pojo) {
        try {
            return gson.toJson(pojo);
        } catch (JsonSyntaxException e) {
            LOGGER.error(MALFORMED_JSON_SYNTAX_MESSAGE, e);
            throw new RuntimeException(e);
        }
    }

    public <T> T deserialize(String json, Class<T> clazz) {
        try {
            return gson.fromJson(json, clazz);
        } catch (JsonSyntaxException e) {
            LOGGER.error(MALFORMED_JSON_SYNTAX_MESSAGE, e);
            throw new RuntimeException(e);
        }
    }

}
