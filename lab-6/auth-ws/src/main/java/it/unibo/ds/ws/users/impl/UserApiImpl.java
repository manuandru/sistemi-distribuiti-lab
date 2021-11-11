package it.unibo.ds.ws.users.impl;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.ConflictResponse;
import io.javalin.http.NotFoundResponse;
import it.unibo.ds.ws.*;
import it.unibo.ds.ws.AbstractApi;
import it.unibo.ds.ws.users.UserApi;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class UserApiImpl extends AbstractApi implements UserApi {

    public UserApiImpl(Authenticator storage) {
        super(storage);
    }

    @Override
    public CompletableFuture<Collection<? extends String>> getAllNames(int skip, int limit, String filter) {
        return CompletableFuture.supplyAsync(
                () -> {
                    throw new Error("not implemented");
                }
        );
    }

    @Override
    public CompletableFuture<String> registerUser(User user) {
        return CompletableFuture.supplyAsync(
                () -> {
                    throw new Error("not implemented");
                }
        );
    }

    @Override
    public CompletableFuture<User> getUser(String userId) {
        return CompletableFuture.supplyAsync(
                () -> {
                    throw new Error("not implemented");
                }
        );
    }

    @Override
    public CompletableFuture<Void> removeUser(String userId) {
        return CompletableFuture.supplyAsync(
                () -> {
                    throw new Error("not implemented");
                }
        );
    }

    @Override
    public CompletableFuture<String> editUser(String userId, User changes) {
        return CompletableFuture.supplyAsync(
                () -> {
                    throw new Error("not implemented");
                }
        );
    }
}
