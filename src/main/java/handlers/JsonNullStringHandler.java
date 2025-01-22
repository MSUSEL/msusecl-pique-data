package handlers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Optional;

public class JsonNullStringHandler implements IJsonNullStringHandler {
    @Override
    public Optional<String> getString(JsonObject json, String key) {
        return Optional.ofNullable(json.get(key))
                .filter(e -> !e.isJsonNull())
                .map(JsonElement::getAsString);
    }
}
