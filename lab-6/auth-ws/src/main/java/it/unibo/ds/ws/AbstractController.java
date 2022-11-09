package it.unibo.ds.ws;

import io.javalin.http.Context;
import it.unibo.ds.ws.utils.Filters;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractController {
    private final String path;

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

    protected <T> void asyncReplyWithBody(Context ctx, String contentType, CompletableFuture<T> futureResult) {
        ctx.contentType(contentType);
        ctx.future(() -> futureResult.thenAccept(ctx::json));
    }

    protected void asyncReplyWithoutBody(Context ctx, String contentType, CompletableFuture<?> futureResult) {
        ctx.contentType(contentType);
        ctx.future(() -> futureResult);
    }
}
