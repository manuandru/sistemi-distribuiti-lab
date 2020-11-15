package sd.lab.ws.presentation.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class NumberSerializer extends AbstractJsonSerializer<Number> {
    @Override
    protected JsonElement toJson(Number object) {
        return new JsonPrimitive(object);
    }
}
