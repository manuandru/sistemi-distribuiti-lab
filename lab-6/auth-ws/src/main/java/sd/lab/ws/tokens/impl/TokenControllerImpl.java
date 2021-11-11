package sd.lab.ws.tokens.impl;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import io.javalin.plugin.openapi.dsl.OpenApiBuilder;
import it.unibo.ds.ws.Credentials;
import sd.lab.ws.AbstractController;
import sd.lab.ws.Doc;
import sd.lab.ws.tokens.TokenApi;
import sd.lab.ws.tokens.TokenController;
import sd.lab.ws.utils.Filters;

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
        Credentials credentials = deserializeBodyAsSingle(Credentials.class, context);

        context.contentType("application/json").future(
                api.createToken(credentials).thenComposeAsync(this::serializeSingleResult)
        );
    }

    @Override
    public void registerRoutes(Javalin app) {
        app.before(path("*"), Filters.ensureClientAcceptsMimeType("application", "json"));
        app.post(path("/"), OpenApiBuilder.documented(Doc.Tokens.postToken, this::postToken));
    }
}
