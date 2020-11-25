package sd.lab.ws.tuplespaces.impl;

import io.javalin.Javalin;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import sd.lab.linda.textual.RegexTemplate;
import sd.lab.linda.textual.StringTuple;
import sd.lab.ws.presentation.Presentation;
import sd.lab.ws.presentation.PresentationException;
import sd.lab.ws.tuplespaces.TextualSpaceApi;
import sd.lab.ws.tuplespaces.TextualSpaceController;
import sd.lab.ws.tuplespaces.TextualSpaceStorage;
import sd.lab.ws.utils.Filters;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

public class TextualSpaceControllerImpl implements TextualSpaceController {

    private final String root;

    public TextualSpaceControllerImpl(String root) {
        this.root = Objects.requireNonNull(root);
    }

    @Override
    public String path() {
        return root;
    }

    @Override
    public void getAll(Context context) throws Exception {
        var api = getApi(context);
        var skip = context.queryParam("skip", Integer.class, "0").check(i -> i >= 0).get();
        var limit = context.queryParam("limit", Integer.class, "10").check(i -> i >= 0).get();
        var filter = context.queryParam("filter", String.class, "").get();
        context.contentType("application/json").result(
                api.getAllNames(skip, limit, filter).thenApply(multipleResultsSerializer(String.class))
        );
    }

    @Override
    public void get(Context context) throws Exception {
        var api = getApi(context);
        var tupleSpaceName = context.pathParam("tupleSpaceName");
        var templateString = context.queryParam("template");
        var count = context.queryParam("count", Boolean.class, "false").get();

        context.contentType("application/json");

        if (count) {
            context.result(
                    api.countTuples(tupleSpaceName).thenApply(singleResultSerializer(Number.class))
            );
        } else if (templateString != null && !templateString.isBlank()) {
            var template = deserializeAs(templateString, RegexTemplate.class);
            context.result(
                    api.readTuple(tupleSpaceName, template).thenApply(singleResultSerializer(StringTuple.class))
            );
        } else {
            context.result(
                    api.getAllTuples(tupleSpaceName).thenApply(multipleResultsSerializer(StringTuple.class))
            );
        }
    }

    @Override
    public void delete(Context context) throws Exception {
        var api = getApi(context);
        var tupleSpaceName = context.pathParam("tupleSpaceName");
        var templateString = context.queryParam("template");

        if (templateString != null && !templateString.isBlank()) {
            var template = deserializeAs(templateString, RegexTemplate.class);
            context.contentType("application/json").result(
                    api.consumeTuple(tupleSpaceName, template).thenApply(singleResultSerializer(StringTuple.class))
            );
        } else {
            throw new BadRequestResponse("Missing template in path");
        }
    }

    @Override
    public void post(Context context) throws Exception {
        var api = getApi(context);
        var tupleSpaceName = context.pathParam("tupleSpaceName");
        var body = context.body();

        if (!body.isBlank()) {
            var tuple = deserializeAs(body, StringTuple.class);
            context.contentType("application/json").result(
                    api.insertTuple(tupleSpaceName, tuple).thenApply(singleResultSerializer(StringTuple.class))
            );
        } else {
            throw new BadRequestResponse("Missing tuple in body");
        }
    }

    @Override
    public String path(String subPath) {
        return path() + Objects.requireNonNull(subPath);
    }

    /**
     * @see Filters#putSingletonInContext(Class, Object)
     * @see Filters#getSingletonFromContext(Class, Context)
     */
    @Override
    public void registerRoutes(Javalin app) {
        var textualSpaceStorage = TextualSpaceStorage.getInstance();

        app.before(path("/*"), Filters.putSingletonInContext(TextualSpaceStorage.class, textualSpaceStorage));
        app.before(path("/*"), Filters.ensureClientAcceptMimeType("application", "json"));
        app.get(path(), this::getAll);
        app.get(path("/:tupleSpaceName"), this::get);
        app.post(path("/:tupleSpaceName"), this::post);
        app.delete(path("/:tupleSpaceName"), this::delete);
    }

    /**
     * @see Filters#putSingletonInContext(Class, Object)
     * @see Filters#getSingletonFromContext(Class, Context)
     */
    private TextualSpaceStorage getStorage(Context context) {
        return Filters.getSingletonFromContext(TextualSpaceStorage.class, context);
    }

    private TextualSpaceApi getApi(Context context) {
        return TextualSpaceApi.of(getStorage(context));
    }

    private <T> Function<T, String> singleResultSerializer(Class<T> klass) {
        return Presentation.serializerOf(klass)::serialize;
    }

    private <T> Function<Collection<? extends T>, String> multipleResultsSerializer(Class<T> klass) {
        return Presentation.serializerOf(klass)::serializeMany;
    }

    private <T> T deserializeAs(String string, Class<T> type) {
        try {
            return Presentation.deserializerOf(type).deserialize(string);
        } catch (PresentationException e) {
            throw new BadRequestResponse("Cannot deserialize " + string + " as " + type.getSimpleName());
        }
    }
}
