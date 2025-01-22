package handlers;

import com.google.gson.JsonObject;

import java.util.Optional;

public interface IJsonNullStringHandler {
    Optional<String> getString(JsonObject json, String key);
}
