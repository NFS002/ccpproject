package net.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public interface BiDirectionalJsonSerializer<T extends Identifiable> extends JsonSerializer<T> {

    JsonElement serializeStub(JsonObject object,T src,JsonSerializationContext jsonSerializationContext);

    JsonElement serializeBody(JsonObject object, T src, JsonSerializationContext jsonSerializationContext);

}
