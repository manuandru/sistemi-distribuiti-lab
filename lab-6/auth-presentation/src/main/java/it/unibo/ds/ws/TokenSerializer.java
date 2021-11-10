package it.unibo.ds.ws;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class TokenSerializer implements JsonSerializer<Token> {

    @Override
    public JsonElement serialize(Token src, Type typeOfSrc, JsonSerializationContext context) {
        var object = new JsonObject();
        object.addProperty("username", src.getUsername());
        object.add("role", context.serialize(src.getRole()));
        return object;
    }
}
