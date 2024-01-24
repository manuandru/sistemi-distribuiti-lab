package it.unibo.ds.ws.utils;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.NotFoundResponse;
import org.eclipse.jetty.http.HttpMethod;

import java.util.EnumSet;
import java.util.Objects;


public class Filters {
    public static <T> Handler putSingletonInContext(Class<T> klass, T singleton) {
        Objects.requireNonNull(singleton);
        return context -> {
            context.attribute(klass.getName(), singleton);
        };
    }

    public static <T> T getSingletonFromContext(Class<T> klass, Context context) {
        return Objects.requireNonNull(context.attribute(klass.getName()));
    }

    public static Handler ensureClientAcceptsMimeType(String type, String subType, EnumSet<HttpMethod> methods) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(subType);
        var mimeType = type + "/" + subType;
        return ctx -> {
            if (methods.stream().map(HttpMethod::name).anyMatch(ctx.method().name()::equalsIgnoreCase)) {
                var accept = ctx.header("Accept");
                if (accept == null || accept.isBlank()
                        || (!accept.contains("*/*") && !accept.contains(type + "/*") && !accept.contains(mimeType))) {
                    throw new NotFoundResponse("Cannot serve request because MIME type " + mimeType + " is not acceptable");
                }
            } else {
                var accept = ctx.header("Accept");
                if (accept != null && !accept.isBlank()) {
                    throw new BadRequestResponse("Unexpected Accept header in request");
                }
            }
        };
    }

    public static Handler ensureClientAcceptsMimeType(String type, String subType) {
        return ensureClientAcceptsMimeType(type, subType, EnumSet.of(HttpMethod.POST, HttpMethod.PUT, HttpMethod.GET));
    }
}
