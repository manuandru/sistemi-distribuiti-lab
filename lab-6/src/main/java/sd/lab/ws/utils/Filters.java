package sd.lab.ws.utils;

import io.javalin.http.Handler;

import java.util.Objects;


public class Filters {
    public static <T> Handler putSingletonInContext(Class<T> klass, T singleton) {
        Objects.requireNonNull(singleton);
        return ctx -> {
            ctx.attribute(klass.getName(), singleton);
        };
    }
}
