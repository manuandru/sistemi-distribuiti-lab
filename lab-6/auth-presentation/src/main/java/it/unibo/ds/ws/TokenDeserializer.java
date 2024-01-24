package it.unibo.ds.ws;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class TokenDeserializer implements JsonDeserializer<Token> {
    @Override
    public Token deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            var object = json.getAsJsonObject();
            var username = object.has("username") ? object.get("username").getAsString() : null;
            Role role = object.has("role") ? context.deserialize(object.get("role"), Role.class) : null;
            return new Token(username, role);
        } catch (ClassCastException e) {
            throw new JsonParseException("Invalid token: " + json, e);
        }
    }
}
