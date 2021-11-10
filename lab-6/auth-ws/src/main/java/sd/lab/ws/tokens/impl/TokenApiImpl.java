package sd.lab.ws.tokens.impl;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.UnauthorizedResponse;
import it.unibo.ds.ws.Authenticator;
import it.unibo.ds.ws.Credentials;
import it.unibo.ds.ws.Token;
import it.unibo.ds.ws.WrongCredentialsException;
import sd.lab.ws.AbstractApi;
import sd.lab.ws.tokens.TokenApi;

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
