package sd.lab.ws.presentation.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import sd.lab.linda.textual.RegexTemplate;
import sd.lab.ws.presentation.PresentationException;

public class RegexTemplateDeserializer extends AbstractJsonDeserializer<RegexTemplate> {
    @Override
    protected RegexTemplate deserializeJson(JsonElement jsonElement) {
        if (jsonElement instanceof JsonObject) {
            var jsonObject = (JsonObject) jsonElement;
            if (jsonObject.has("template")) {
                var templateElement = jsonObject.get("template");
                if (templateElement instanceof JsonPrimitive) {
                    return RegexTemplate.of(templateElement.getAsString());
                }
            }
        }
        throw new PresentationException("Cannot convert " + jsonElement + " into a " + RegexTemplate.class.getSimpleName());
    }
}
