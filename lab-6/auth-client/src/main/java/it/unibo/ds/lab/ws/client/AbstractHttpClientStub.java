package it.unibo.ds.lab.ws.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import it.unibo.ds.ws.ConflictException;
import it.unibo.ds.ws.GsonUtils;
import it.unibo.ds.ws.WrongCredentialsException;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Function;

public abstract class AbstractHttpClientStub {

    private static final HttpResponse.BodyHandler<String> BODY_TO_STRING = HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);

    private final HttpClient client = HttpClient.newHttpClient();
    private final URI serviceURI;
    private final Gson gson = GsonUtils.createGson();

    public AbstractHttpClientStub(URI host, String root, String version) {
        Objects.requireNonNull(host);
        this.serviceURI = host.resolve(String.format("/%s/v%s", root, version));
    }

    protected static URI queryParam(URI base, String key, Object value) {
        String prefix = base.getQuery() == null ? "?" : "&";
        String keyValue = prefix + key + "=" + URLEncoder.encode(value.toString(), StandardCharsets.UTF_8);
        return URI.create(base.toString() + keyValue);
    }

    protected static URI queryParam(URI base, String key1, Object value1, String key2, Object value2) {
        return queryParam(queryParam(base, key1, value1), key2, value2);
    }

    protected URI resourceUri(String resource) {
        return serviceURI.resolve(serviceURI.getPath() + resource);
    }

    protected URI resourceUriWithQuery(String resource, String key, Object value) {
        return queryParam(resourceUri(resource), key, value);
    }

    protected URI resourceUriWithQuery(String resource, String key1, Object value1, String key2, Object value2) {
        return queryParam(resourceUri(resource), key1, value1, key2, value2);
    }

    protected <T> String serialize(T object) {
        return gson.toJson(object);
    }

    protected <T> Function<String, CompletableFuture<T>> deserializeOne(Class<T> type) {
        return toBeDeserialized -> {
            CompletableFuture<T> promise = new CompletableFuture<>();
            try {
                promise.complete(gson.fromJson(toBeDeserialized, type));
            } catch (JsonParseException e) {
                promise.completeExceptionally(e);
            }
            return promise;
        };
    }

    protected <T> Function<String, CompletableFuture<List<T>>> deserializeMany(Class<T> type) {
        return toBeDeserialized -> {
            CompletableFuture<List<T>> promise = new CompletableFuture<>();
            try {
                JsonElement jsonElement = gson.fromJson(toBeDeserialized, JsonElement.class);
                if (jsonElement.isJsonArray()) {
                    JsonArray jsonArray = jsonElement.getAsJsonArray();
                    List<T> items = new ArrayList<>(jsonArray.size());
                    for (JsonElement item : jsonArray) {
                        items.add(gson.fromJson(item, type));
                    }
                    promise.complete(items);
                } else {
                    promise.completeExceptionally(new IllegalStateException("Cannot deserialize: " + toBeDeserialized));
                }
            } catch (JsonParseException e) {
                promise.completeExceptionally(new IllegalStateException(e));
            }
            return promise;
        };
    }

    protected CompletableFuture<HttpResponse<String>> sendRequestToClient(HttpRequest request) {
        return client.sendAsync(request, BODY_TO_STRING);
    }

    protected HttpRequest.BodyPublisher body(Object object) {
        return HttpRequest.BodyPublishers.ofString(serialize(object), StandardCharsets.UTF_8);
    }

    protected Function<HttpResponse<String>, CompletableFuture<String>> checkResponse() {
        return response -> {
            if (response.statusCode() == 200) {
                return CompletableFuture.completedFuture(response.body());
            } else if (response.statusCode() == 400) { // bad content
                return CompletableFuture.failedFuture(new IllegalArgumentException(response.body()));
            } else if (response.statusCode() == 401) { // unauthorized
                return CompletableFuture.failedFuture(new WrongCredentialsException(response.body()));
            } else if (response.statusCode() == 409) { // conflict
                return CompletableFuture.failedFuture(new ConflictException(response.body()));
            } else {
                return CompletableFuture.failedFuture(
                        new IllegalStateException(
                                String.format(
                                        "Unexpected response while %s %s: %d",
                                        response.request().method(),
                                        response.uri(),
                                        response.statusCode()
                                )
                        )
                );
            }
        };
    }

    protected <E extends Exception> E getCauseAs(CompletionException e, Class<E> type) {
        if (type.isAssignableFrom(e.getCause().getClass())) {
            return type.cast(e.getCause());
        } else if (e.getCause() instanceof RuntimeException) {
            throw (RuntimeException) e.getCause();
        } else {
            throw e;
        }
    }
}
