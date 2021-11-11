package it.unibo.ds.ws.tokens;

import it.unibo.ds.ws.Authenticator;
import it.unibo.ds.ws.Credentials;
import it.unibo.ds.ws.Token;
import it.unibo.ds.ws.tokens.impl.TokenApiImpl;

import java.util.concurrent.CompletableFuture;

public interface TokenApi {

    CompletableFuture<Token> createToken(Credentials credentials);

    static TokenApi of(Authenticator storage) {
        return new TokenApiImpl(storage);
    }
}
