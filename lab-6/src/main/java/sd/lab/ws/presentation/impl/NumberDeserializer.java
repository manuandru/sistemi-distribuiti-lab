package sd.lab.ws.presentation.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import sd.lab.ws.presentation.PresentationException;

public class NumberDeserializer extends AbstractJsonDeserializer<Number> {
    @Override
    protected Number deserializeJson(JsonElement jsonElement) {
        if (jsonElement instanceof JsonPrimitive) {
            var jsonPrimitive = (JsonPrimitive) jsonElement;
            return jsonPrimitive.getAsNumber();
        }
        throw new PresentationException("Cannot convert " + jsonElement + " into a " + Number.class.getSimpleName());
    }
}
