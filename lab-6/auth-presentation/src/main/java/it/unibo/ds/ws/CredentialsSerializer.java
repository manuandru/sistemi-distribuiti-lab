package it.unibo.ds.ws;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class CredentialsSerializer implements JsonSerializer<Credentials> {

    @Override
    public JsonElement serialize(Credentials src, Type typeOfSrc, JsonSerializationContext context) {
        var object = new JsonObject();
        object.addProperty("user_id", src.getUserId());
        object.addProperty("password", src.getPassword());
        return object;
    }
}
