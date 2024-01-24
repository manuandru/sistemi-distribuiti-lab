package it.unibo.ds.ws.tokens.impl;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.UnauthorizedResponse;
import it.unibo.ds.ws.*;
import it.unibo.ds.ws.tokens.TokenApi;
import it.unibo.ds.ws.AbstractApi;

import java.util.concurrent.CompletableFuture;

public class TokenApiImpl extends AbstractApi implements TokenApi {
    public TokenApiImpl(Authenticator storage) {
        super(storage);
    }

    @Override
    public CompletableFuture<Token> createToken(Credentials credentials) {
        return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        return storage().authorize(credentials);
                    } catch (WrongCredentialsException e) {
                        throw new UnauthorizedResponse(e.getMessage());
                    } catch (IllegalArgumentException e) {
                        throw new BadRequestResponse(e.getMessage());
                    }
                }
        );
    }
}
