package it.unibo.ds.ws;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class CredentialsDeserializer implements JsonDeserializer<Credentials> {
    @Override
    public Credentials deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            var object = json.getAsJsonObject();
            var username = object.get("user_id").getAsString();
            var password = object.get("password").getAsString();
            return new Credentials(username, password);
        } catch (ClassCastException | NullPointerException | UnsupportedOperationException e) {
            throw new JsonParseException("Invalid credentials: " + json, e);
        }
    }
}
