package it.unibo.ds.ws.tokens.impl;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import it.unibo.ds.ws.AbstractController;
import it.unibo.ds.ws.Credentials;
import it.unibo.ds.ws.tokens.TokenApi;
import it.unibo.ds.ws.tokens.TokenController;
import it.unibo.ds.ws.utils.Filters;

public class TokenControllerImpl extends AbstractController implements TokenController {
    public TokenControllerImpl(String path) {
        super(path);
    }

    private TokenApi getApi(Context context) {
        return TokenApi.of(getAuthenticatorInstance(context));
    }

    @Override
    public void postToken(Context context) throws HttpResponseException {
        TokenApi api = getApi(context);
        var credentials = context.bodyAsClass(Credentials.class);
        var futureResult = api.createToken(credentials);
        asyncReplyWithBody(context, "application/json", futureResult);
    }

    @Override
    public void registerRoutes(Javalin app) {
        app.before(path("*"), Filters.ensureClientAcceptsMimeType("application", "json"));
        app.post(path("/"), this::postToken);
    }
}
