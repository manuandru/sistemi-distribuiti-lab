package it.unibo.ds.ws.users.impl;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import it.unibo.ds.ws.AbstractController;
import it.unibo.ds.ws.User;
import it.unibo.ds.ws.users.UserApi;
import it.unibo.ds.ws.users.UserController;
import it.unibo.ds.ws.utils.Filters;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

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
        int skip = getOptionalIntParam(context, "skip", 0);
        int limit = getOptionalIntParam(context, "limit", 10);
        String filter = getOptionalStringParam(context, "filter", "");
        CompletableFuture<Collection<? extends String>> futureResult = api.getAllNames(skip, limit, filter);
        asyncReplyWithBody(context, "application/json", futureResult);
    }

    @Override
    public void postNewUser(Context context) throws HttpResponseException {
        UserApi api = getApi(context);
        var user = context.bodyAsClass(User.class);
        CompletableFuture<String> futureResult = api.registerUser(user);
        asyncReplyWithBody(context, "application/json", futureResult);
    }

    @Override
    public void deleteUser(Context context) throws HttpResponseException {
        UserApi api = getApi(context);
        String userId = context.pathParam("userId");
        CompletableFuture<Void> futureResult = api.removeUser(userId);
        asyncReplyWithoutBody(context, "application/json", futureResult);
        ;
    }

    @Override
    public void getUser(Context context) throws HttpResponseException {
        UserApi api = getApi(context);
        String userId = context.pathParam("userId");
        CompletableFuture<User> futureResult = api.getUser(userId);
        asyncReplyWithBody(context, "application/json", futureResult);
    }

    @Override
    public void putUser(Context context) throws HttpResponseException {
        UserApi api = getApi(context);
        String userId = context.pathParam("userId");
        var user = context.bodyAsClass(User.class);
        CompletableFuture<String> futureResult = api.editUser(userId, user);
        asyncReplyWithBody(context, "application/json", futureResult);
    }

    @Override
    public void registerRoutes(Javalin app) {
        app.before(path("*"), Filters.ensureClientAcceptsMimeType("application", "json"));
        app.get(path(), this::getAllUserNames);
        app.post(path(), this::postNewUser);
        app.get(path("/{userId}"), this::getUser);
        app.delete(path("/{userId}"), this::deleteUser);
        app.put(path("/{userId}"), this::putUser);
    }
}
