package sd.lab.ws.presentation.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.javalin.http.BadRequestResponse;
import sd.lab.linda.textual.StringTuple;

public class StringTupleDeserializer extends AbstractJsonDeserializer<StringTuple> {
    @Override
    protected StringTuple deserializeJson(JsonElement jsonElement) {
        if (jsonElement instanceof JsonObject) {
            var jsonObject = (JsonObject) jsonElement;
            if (jsonObject.has("tuple")) {
                var tupleElement = jsonObject.get("tuple");
                if (tupleElement instanceof JsonPrimitive) {
                    return StringTuple.of(tupleElement.getAsString());
                }
            }
        }
        throw new BadRequestResponse("Cannot convert " + jsonElement + " into a " + StringTuple.class.getSimpleName());
    }
}
