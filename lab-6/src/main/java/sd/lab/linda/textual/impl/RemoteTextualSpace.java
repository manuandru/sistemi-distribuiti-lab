package sd.lab.linda.textual.impl;

import kotlin.text.Charsets;
import org.apache.commons.collections4.MultiSet;
import sd.lab.linda.core.Template;
import sd.lab.linda.textual.RegexTemplate;
import sd.lab.linda.textual.StringTuple;
import sd.lab.linda.textual.TextualSpace;
import sd.lab.ws.presentation.Deserializer;
import sd.lab.ws.presentation.Presentation;
import sd.lab.ws.presentation.Serializer;
import sd.lab.ws.presentation.impl.StringTupleSerializer;

import java.io.ObjectStreamException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static sd.lab.ws.presentation.Presentation.deserializerOf;
import static sd.lab.ws.presentation.Presentation.serializerOf;

public class RemoteTextualSpace implements TextualSpace {

    private static final BodyHandler<String> BODY_TO_STRING = BodyHandlers.ofString(StandardCharsets.UTF_8);

    private final HttpClient client = HttpClient.newHttpClient();
    private final URI host;
    private final String name;
    private final URI tupleSpace;

    public RemoteTextualSpace(URI host, String name) {
        this.host = Objects.requireNonNull(host);
        this.name = Objects.requireNonNull(name);
        this.tupleSpace = host.resolve("/linda/v1/tuple-spaces/" + name);
    }

    private static URI queryParam(URI base, String key, Object value) {
        var prefix = base.getQuery() == null ? "?" : "&";
        var keyValue = prefix + key + "=" + URLEncoder.encode(value.toString(), StandardCharsets.UTF_8);
        return base.resolve(keyValue);
    }

    private static URI queryParam(URI base, String key1, Object value1, String key2, Object value2) {
        return queryParam(queryParam(base, key1, value1), key2, value2);
    }

    private URI tupleSpaceWithQuery(String key, Object value) {
        return queryParam(tupleSpace, key, value);
    }

    private URI tupleSpaceWithQuery(String key1, Object value1, String key2, Object value2) {
        return queryParam(tupleSpace, key1, value1, key2, value2);
    }

    @Override
    public CompletableFuture<StringTuple> rd(RegexTemplate template) {
        var request = HttpRequest.newBuilder()
                .uri(tupleSpaceWithQuery("template", serializerOf(RegexTemplate.class).serialize(template)))
                .GET()
                .build();
        return client.sendAsync(request, BODY_TO_STRING)
                .thenApply(HttpResponse::body)
                .thenApply(deserializerOf(StringTuple.class)::deserialize);
    }

    @Override
    public CompletableFuture<StringTuple> in(RegexTemplate template) {
        return null;
    }

    @Override
    public CompletableFuture<StringTuple> out(StringTuple tuple) {
        return null;
    }

    @Override
    public CompletableFuture<MultiSet<? extends StringTuple>> get() {
        return null;
    }

    @Override
    public CompletableFuture<Integer> count() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}
