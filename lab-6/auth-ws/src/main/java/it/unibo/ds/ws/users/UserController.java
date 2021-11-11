package it.unibo.ds.ws.users;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import it.unibo.ds.ws.users.impl.UserControllerImpl;

public interface UserController {

    void getAllUserNames(Context context) throws HttpResponseException;

    void postNewUser(Context context) throws HttpResponseException;

    void deleteUser(Context context) throws HttpResponseException;

    void getUser(Context context) throws HttpResponseException;

    void putUser(Context context) throws HttpResponseException;

    String path();

    String path(String subPath);

    void registerRoutes(Javalin app);

    static UserController of(String root) {
        return new UserControllerImpl(root);
    }
}
