package sd.lab.ws.presentation.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class StringSerializer extends AbstractJsonSerializer<String> {
    @Override
    protected JsonElement toJson(String object) {
        return new JsonPrimitive(object);
    }
}
