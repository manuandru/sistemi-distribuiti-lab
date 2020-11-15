package sd.lab.ws.presentation.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.javalin.http.BadRequestResponse;

public class StringDeserializer extends AbstractJsonDeserializer<String> {
    @Override
    protected String deserializeJson(JsonElement jsonElement) {
        if (jsonElement instanceof JsonPrimitive) {
            var jsonPrimitive = (JsonPrimitive) jsonElement;
            return jsonPrimitive.getAsString();
        }
        throw new BadRequestResponse("Cannot convert " + jsonElement + " into a " + String.class.getSimpleName());
    }
}
