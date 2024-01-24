package it.unibo.ds.ws;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserDeserializer implements JsonDeserializer<User> {

    private String getPropertyAsString(JsonObject object, String name) {
        if (object.has(name)) {
            JsonElement value = object.get(name);
            if (value.isJsonNull()) return null;
            return value.getAsString();
        }
        return null;
    }

    private <T> T getPropertyAs(JsonObject object, String name, Class<T> type, JsonDeserializationContext context) {
        if (object.has(name)) {
            JsonElement value = object.get(name);
            if (value.isJsonNull()) return null;
            return context.deserialize(value, type);
        }
        return null;
    }

    @Override
    public User deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            var object = json.getAsJsonObject();
            var fullName = getPropertyAsString(object, "full_name");
            var username = getPropertyAsString(object, "username");
            var password = getPropertyAsString(object, "password");
            Role role = getPropertyAs(object, "role", Role.class, context);
            LocalDate birthDate = getPropertyAs(object, "birth_date", LocalDate.class, context);
            var emailsArray = object.getAsJsonArray("email_addresses");
            List<String> emails = new ArrayList<>(emailsArray.size());
            for (var item : emailsArray) {
                if (item.isJsonNull()) continue;
                emails.add(item.getAsString());
            }
            return new User(fullName, username, password, birthDate, role, emails);
        } catch (ClassCastException e) {
            throw new JsonParseException("Invalid user: " + json, e);
        }
    }
}
