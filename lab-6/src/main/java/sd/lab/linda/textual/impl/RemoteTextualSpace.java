package sd.lab.linda.textual.impl;


import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.multiset.HashMultiSet;
import sd.lab.linda.core.RemoteException;
import sd.lab.linda.textual.RegexTemplate;
import sd.lab.linda.textual.StringTuple;
import sd.lab.linda.textual.TextualSpace;
import sd.lab.ws.Service;
import sd.lab.ws.presentation.Presentation;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class RemoteTextualSpace implements TextualSpace {

    private static final BodyHandler<String> BODY_TO_STRING = BodyHandlers.ofString(StandardCharsets.UTF_8);

    private final HttpClient client = HttpClient.newHttpClient();
    private final String name;
    private final URI tupleSpaceUri;

    public RemoteTextualSpace(URI host, String name) {
        Objects.requireNonNull(host);
        this.name = Objects.requireNonNull(name);
        this.tupleSpaceUri = host.resolve("/linda/v" + Service.API_VERSION + "/tuple-spaces/" + name);
    }

    private static URI queryParam(URI base, String key, Object value) {
        var prefix = base.getQuery() == null ? "?" : "&";
        var keyValue = prefix + key + "=" + URLEncoder.encode(value.toString(), StandardCharsets.UTF_8);
        return URI.create(base.toString() + keyValue);
    }

    private static URI queryParam(URI base, String key1, Object value1, String key2, Object value2) {
        return queryParam(queryParam(base, key1, value1), key2, value2);
    }

    private URI tupleSpaceUriWithQuery(String key, Object value) {
        return queryParam(tupleSpaceUri, key, value);
    }

    private URI tupleSpaceUriWithQuery(String key1, Object value1, String key2, Object value2) {
        return queryParam(tupleSpaceUri, key1, value1, key2, value2);
    }

    private static <T> String serialize(T object) {
        return Presentation.serializerOf((Class<T>) object.getClass()).serialize(object);
    }

    private static <T> Function<String, T> deserializeOne(Class<T> klass) {
        return Presentation.deserializerOf(klass)::deserialize;
    }

    private static <T> Function<String, List<T>> deserializeMany(Class<T> klass) {
        return Presentation.deserializerOf(klass)::deserializeMany;
    }

    private CompletableFuture<HttpResponse<String>> sendRequestToClient(HttpRequest request) {
        return client.sendAsync(request, BODY_TO_STRING);
    }

    private static HttpRequest.BodyPublisher body(Object object) {
        return HttpRequest.BodyPublishers.ofString(serialize(object), StandardCharsets.UTF_8);
    }

    private String responseChecker(HttpResponse<String> response) {
        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new RemoteException(
                    String.format(
                            "Unexpected response while %s %s: %d",
                            response.request().method(),
                            response.uri(),
                            response.statusCode()
                    )
            );
        }
    }

    @Override
    public CompletableFuture<StringTuple> rd(RegexTemplate template) {
        var request = HttpRequest.newBuilder()
                .uri(tupleSpaceUriWithQuery("template", serialize(template)))
                .GET()
                .build();
        return sendRequestToClient(request)
                .thenApply(this::responseChecker)
                .thenApply(deserializeOne(StringTuple.class));
    }

    @Override
    public CompletableFuture<StringTuple> in(RegexTemplate template) {
        var request = HttpRequest.newBuilder()
                .uri(tupleSpaceUriWithQuery("template", serialize(template)))
                .DELETE()
                .build();
        return sendRequestToClient(request)
                .thenApply(this::responseChecker)
                .thenApply(deserializeOne(StringTuple.class));
    }

    @Override
    public CompletableFuture<StringTuple> out(StringTuple tuple) {
        var request = HttpRequest.newBuilder()
                .uri(tupleSpaceUri)
                .POST(body(tuple))
                .build();
        return sendRequestToClient(request)
                .thenApply(this::responseChecker)
                .thenApply(deserializeOne(StringTuple.class));
    }

    @Override
    public CompletableFuture<MultiSet<? extends StringTuple>> get() {
        var request = HttpRequest.newBuilder()
                .uri(tupleSpaceUri)
                .GET()
                .build();
        return sendRequestToClient(request)
                .thenApply(this::responseChecker)
                .thenApply(deserializeMany(StringTuple.class))
                .thenApply(HashMultiSet::new);
    }

    @Override
    public CompletableFuture<Integer> count() {
        var request = HttpRequest.newBuilder()
                .uri(tupleSpaceUriWithQuery("count", true))
                .GET()
                .build();
        return sendRequestToClient(request)
                .thenApply(this::responseChecker)
                .thenApply(deserializeOne(Number.class))
                .thenApply(Number::intValue);
    }

    @Override
    public String getName() {
        return name;
    }
}
