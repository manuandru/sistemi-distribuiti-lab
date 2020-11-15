package sd.lab.ws.presentation.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.javalin.http.BadRequestResponse;

public class NumberDeserializer extends AbstractJsonDeserializer<Number> {
    @Override
    protected Number deserializeJson(JsonElement jsonElement) {
        if (jsonElement instanceof JsonPrimitive) {
            var jsonPrimitive = (JsonPrimitive) jsonElement;
            return jsonPrimitive.getAsNumber();
        }
        throw new BadRequestResponse("Cannot convert " + jsonElement + " into a " + Number.class.getSimpleName());
    }
}
