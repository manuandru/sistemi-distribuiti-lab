package sd.lab.ws.presentation.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import sd.lab.linda.textual.StringTuple;

public class StringTupleSerializer extends AbstractJsonSerializer<StringTuple> {
    @Override
    protected JsonElement toJsonElement(StringTuple object) {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("tuple", object.getValue());
        return jsonObject;
    }
}
