package sd.lab.ws.users.impl;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import io.javalin.plugin.openapi.dsl.OpenApiBuilder;
import it.unibo.ds.ws.User;
import sd.lab.ws.AbstractController;
import sd.lab.ws.Doc;
import sd.lab.ws.users.UserApi;
import sd.lab.ws.users.UserController;
import sd.lab.ws.utils.Filters;

public class UserControllerImpl extends AbstractController implements UserController {

    public UserControllerImpl(String path) {
        super(path);
    }

    private UserApi getApi(Context context) {
        return UserApi.of(getAuthenticatorInstance(context));
    }

    @Override
    public void getAllUserNames(Context context) throws HttpResponseException {
        UserApi api = getApi(context);
        var skip = getOptionalIntParam(context, "skip", 0);
        var limit = getOptionalIntParam(context, "limit", 10);
        var filter = getOptionalStringParam(context, "filter", "");
        context.contentType("application/json").future(
                api.getAllNames(skip, limit, filter).thenComposeAsync(this::serializeMultipleResults)
        );
    }

    @Override
    public void postNewUser(Context context) throws HttpResponseException {
        var api = getApi(context);
        User user = deserializeBodyAsSingle(User.class, context);
        context.contentType("application/json").future(
                api.registerUser(user).thenComposeAsync(this::serializeSingleResult)
        );
    }

    @Override
    public void deleteUser(Context context) throws HttpResponseException {
        var api = getApi(context);
        var userId = context.pathParam("userId");
        context.future(api.removeUser(userId).thenComposeAsync(this::voidResult));
    }

    @Override
    public void getUser(Context context) throws HttpResponseException {
        var api = getApi(context);
        var userId = context.pathParam("userId");
        context.future(api.getUser(userId).thenComposeAsync(this::serializeSingleResult));
    }

    @Override
    public void putUser(Context context) throws HttpResponseException {
        var api = getApi(context);
        var userId = context.pathParam("userId");
        User user = deserializeBodyAsSingle(User.class, context);
        context.future(api.getUser(userId).thenComposeAsync(this::serializeSingleResult));
        context.future(api.editUser(userId, user).thenComposeAsync(this::serializeSingleResult));
    }

    @Override
    public void registerRoutes(Javalin app) {
        app.before(path("*"), Filters.ensureClientAcceptsMimeType("application", "json"));
        app.get(path(), OpenApiBuilder.documented(Doc.Users.getAllUserNames, this::getAllUserNames));
        app.post(path(), OpenApiBuilder.documented(Doc.Users.postNewUser, this::postNewUser));
        app.get(path("/{userId}"), OpenApiBuilder.documented(Doc.Users.getUser, this::getUser));
        app.delete(path("/{userId}"), OpenApiBuilder.documented(Doc.Users.deleteUser, this::deleteUser));
        app.put(path("/{userId}"), OpenApiBuilder.documented(Doc.Users.putUser, this::putUser));
    }
}
