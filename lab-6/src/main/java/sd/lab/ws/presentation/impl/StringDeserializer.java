package sd.lab.ws.presentation.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import sd.lab.ws.presentation.PresentationException;

public class StringDeserializer extends AbstractJsonDeserializer<String> {
    @Override
    protected String deserializeJson(JsonElement jsonElement) {
        if (jsonElement instanceof JsonPrimitive) {
            var jsonPrimitive = (JsonPrimitive) jsonElement;
            return jsonPrimitive.getAsString();
        }
        throw new PresentationException("Cannot convert " + jsonElement + " into a " + String.class.getSimpleName());
    }
}
