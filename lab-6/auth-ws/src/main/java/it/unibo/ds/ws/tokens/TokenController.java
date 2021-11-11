package it.unibo.ds.ws.tokens;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import it.unibo.ds.ws.tokens.impl.TokenControllerImpl;

public interface TokenController {

    String path();

    void postToken(Context context) throws HttpResponseException;

    String path(String subPath);

    void registerRoutes(Javalin app);

    static TokenController of(String root) {
        return new TokenControllerImpl(root);
    }
}
