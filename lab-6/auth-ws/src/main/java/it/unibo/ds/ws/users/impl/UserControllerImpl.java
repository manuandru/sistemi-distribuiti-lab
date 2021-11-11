package it.unibo.ds.ws.users.impl;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import it.unibo.ds.ws.AbstractController;
import it.unibo.ds.ws.users.UserApi;
import it.unibo.ds.ws.users.UserController;

public class UserControllerImpl extends AbstractController implements UserController {

    public UserControllerImpl(String path) {
        super(path);
    }

    private UserApi getApi(Context context) {
        return UserApi.of(getAuthenticatorInstance(context));
    }

    @Override
    public void getAllUserNames(Context context) throws HttpResponseException {
        throw new Error("not implemented");
    }

    @Override
    public void postNewUser(Context context) throws HttpResponseException {
        throw new Error("not implemented");
    }

    @Override
    public void deleteUser(Context context) throws HttpResponseException {
        throw new Error("not implemented");
    }

    @Override
    public void getUser(Context context) throws HttpResponseException {
        throw new Error("not implemented");
    }

    @Override
    public void putUser(Context context) throws HttpResponseException {
        throw new Error("not implemented");
    }

    @Override
    public void registerRoutes(Javalin app) {
        // TODO implement
    }
}
