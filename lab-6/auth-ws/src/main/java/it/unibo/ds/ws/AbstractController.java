package it.unibo.ds.ws;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import it.unibo.ds.ws.utils.Filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractController {
    private final String path;
    private final Gson gson = GsonUtils.createGson();

    public AbstractController(String path) {
        this.path = path;
    }

    protected Authenticator getAuthenticatorInstance(Context context) {
        return Filters.getSingletonFromContext(Authenticator.class, context);
    }

    public String path() {
        return path;
    }

    public String path(String subPath) {
        return path() + subPath;
    }

    protected int getOptionalIntParam(Context context, String name, int defaultValue) {
        var value = context.queryParamAsClass(name, Integer.class)
                .allowNullable()
                .check(i -> i >= 0, "Parameter cannot be negative: " + name)
                .get();
        return Objects.requireNonNullElse(value, defaultValue);
    }

    protected String getOptionalStringParam(Context context, String name, String defaultValue) {
        var value = context.queryParamAsClass(name, String.class)
                .allowNullable()
                .get();
        return Objects.requireNonNullElse(value, defaultValue);
    }

    protected <T> CompletableFuture<String> voidResult(T ignored) {
        try {
            return CompletableFuture.completedFuture("");
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    protected <T> CompletableFuture<String> serializeMultipleResults(Iterable<? super T> objects) {
        try {
            var jsonArray = new JsonArray();
            for (var object : objects) {
                jsonArray.add(gson.toJsonTree(object));
            }
            return serializeSingleResult(jsonArray);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    protected <T> CompletableFuture<String> serializeSingleResult(T object) {
        try {
            String serialized = gson.toJson(object);
            return CompletableFuture.completedFuture(serialized);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    protected <T> T deserializeBodyAsSingle(Class<T> type, Context context) {
        try {
            return gson.fromJson(context.body(), type);
        } catch (JsonParseException e) {
            throw new BadRequestResponse(e.getMessage());
        }
    }

    protected <T> Collection<? extends T> deserializeBodyAsMultiple(Class<T> type, Context context) {
        try {
            var jsonElement = gson.fromJson(context.req.getReader(), JsonElement.class);
            if (jsonElement.isJsonArray()) {
                var jsonArray = jsonElement.getAsJsonArray();
                List<T> items = new ArrayList<>(jsonArray.size());
                for (JsonElement item : jsonArray) {
                    items.add(gson.fromJson(item, type));
                }
                return items;
            }
            throw new BadRequestResponse("Cannot parse " + type.getSimpleName());
        } catch (IOException e) {
            throw new BadRequestResponse("Cannot parse " + type.getSimpleName());
        } catch (JsonParseException e) {
            throw new BadRequestResponse(e.getMessage());
        }
    }
}
