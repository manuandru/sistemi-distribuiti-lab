package sd.lab.ws.presentation.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import sd.lab.linda.textual.RegexTemplate;

public class RegexTemplateSerializer extends AbstractJsonSerializer<RegexTemplate> {
    @Override
    protected JsonElement toJson(RegexTemplate object) {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("template", object.getRegex().pattern());
        return jsonObject;
    }
}
