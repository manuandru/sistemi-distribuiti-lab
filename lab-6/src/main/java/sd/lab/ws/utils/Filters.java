package sd.lab.ws.utils;

import io.javalin.http.Handler;
import io.javalin.http.NotFoundResponse;

import java.util.Objects;


public class Filters {
    public static <T> Handler putSingletonInContext(Class<T> klass, T singleton) {
        Objects.requireNonNull(singleton);
        return ctx -> {
            ctx.attribute(klass.getName(), singleton);
        };
    }

    public static Handler ensureClientAcceptMimeType(String type, String subType) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(subType);
        var mimeType = type + "/" + subType;
        return ctx -> {
            var accept = ctx.header("Accept");
            if (accept == null || accept.isBlank()
                    || (!accept.contains("*/*") && !accept.contains(type + "/*") && !accept.contains(mimeType))) {
                throw new NotFoundResponse("Cannot serve request because MIME type " + mimeType + " is not acceptable");
            }
        };
    }
}
