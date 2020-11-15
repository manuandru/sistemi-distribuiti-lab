package sd.lab.ws.utils;

import io.javalin.http.Handler;

import java.util.Objects;


public class Filters {
    public static <T> Handler putSingleton(T singleton) {
        Objects.requireNonNull(singleton);
        return ctx -> {
            ctx.attribute(singleton.getClass().getName(), singleton);
            ctx.redirect(ctx.path());
        };
    }
}
