package it.unibo.ds.ws;


import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import io.javalin.http.BadRequestResponse;
import io.javalin.json.JsonMapper;

import java.lang.reflect.Type;
import java.util.Objects;

public class JavalinGsonAdapter implements JsonMapper {

    private final Gson gson;

    public JavalinGsonAdapter(Gson gson) {
        this.gson = Objects.requireNonNull(gson);
    }

    @Override
    public String toJsonString(Object obj, Type type) {
        return gson.toJson(obj, type);
    }

    @Override
    public <T> T fromJsonString(String json, Type targetType) {
//        try {
        return gson.fromJson(json, targetType);
//        } catch (JsonParseException e) {
//            TODO throw adequate exception
//        }
    }
}
