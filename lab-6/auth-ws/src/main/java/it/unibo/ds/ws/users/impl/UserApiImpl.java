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
                () -> storage().getAll().stream()
                        .map(User::getFullName)
                        .filter(fullName -> fullName.contains(filter))
                        .skip(skip)
                        .limit(limit)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public CompletableFuture<String> registerUser(User user) {
        return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        storage().register(user);
                        return user.getUsername();
                    } catch (ConflictException e) {
                        throw new ConflictResponse(e.getMessage());
                    }
                }
        );
    }

    @Override
    public CompletableFuture<User> getUser(String userId) {
        return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        return storage().get(userId);
                    } catch (MissingException e) {
                        throw new NotFoundResponse(e.getMessage());
                    }
                }
        );
    }

    @Override
    public CompletableFuture<Void> removeUser(String userId) {
        return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        storage().remove(userId);
                        return null;
                    } catch (MissingException e) {
                        throw new NotFoundResponse(e.getMessage());
                    }
                }
        );
    }

    @Override
    public CompletableFuture<String> editUser(String userId, User changes) {
        return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        storage().edit(userId, changes);
                        return changes.getUsername();
                    } catch (MissingException e) {
                        throw new NotFoundResponse(e.getMessage());
                    } catch (ConflictException e) {
                        throw new ConflictResponse(e.getMessage());
                    }
                }
        );
    }
}
