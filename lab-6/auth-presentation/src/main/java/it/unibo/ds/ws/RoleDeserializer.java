package it.unibo.ds.ws;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class RoleDeserializer implements JsonDeserializer<Role> {
    @Override
    public Role deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            return Role.valueOf(json.getAsString().toUpperCase());
        } catch (IllegalArgumentException | ClassCastException e) {
            throw new JsonParseException("Invalid role: " + json, e);
        }
    }
}
