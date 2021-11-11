package it.unibo.ds.lab.ws.client;

import it.unibo.ds.ws.*;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

public class RemoteAuthenticator extends AbstractHttpClientStub implements Authenticator {

    public RemoteAuthenticator(URI host) {
        super(host, "auth", "0.1.0");
    }

    public RemoteAuthenticator(String host, int port) {
        this(URI.create("http://" + host + ":" + port));
    }

    private CompletableFuture<Token> authorizeAsync(Credentials credentials) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(resourceUri("/tokens"))
                .header("Accept", "application/json")
                .POST(body(credentials))
                .build();
        return sendRequestToClient(request)
                .thenComposeAsync(checkResponse())
                .thenComposeAsync(deserializeOne(Token.class));
    }

    @Override
    public Token authorize(Credentials credentials) throws WrongCredentialsException {
        try {
            return authorizeAsync(credentials).join();
        } catch (CompletionException e) {
            throw getCauseAs(e, WrongCredentialsException.class);
        }
    }

    private CompletableFuture<?> registerAsync(User user) {
        throw new Error("not implemented");
    }

    @Override
    public void register(User user) throws ConflictException {
        try {
            registerAsync(user).join();
        } catch (CompletionException e) {
            throw new Error("not implemented");
        }
    }

    private CompletableFuture<?> removeAsync(String userId) {
        throw new Error("not implemented");
    }

    @Override
    public void remove(String userId) throws MissingException {
        try {
            removeAsync(userId).join();
        } catch (CompletionException e) {
            throw new Error("not implemented");
        }
    }

    private CompletableFuture<User> getAsync(String userId) {
        throw new Error("not implemented");
    }

    @Override
    public User get(String userId) throws MissingException {
        try {
            return getAsync(userId).join();
        } catch (CompletionException e) {
            throw getCauseAs(e, MissingException.class);
        }
    }

    private CompletableFuture<?> editAsync(String userId, User changes) {
        throw new Error("not implemented");
    }

    @Override
    public void edit(String userId, User changes) throws MissingException, ConflictException {
        try {
            editAsync(userId, changes).join();
        } catch (CompletionException e) {
            throw new Error("not implemented");
        }
    }

    protected CompletableFuture<List<String>> getAllNamesAsync() {
        throw new Error("not implemented");
    }

    @Override
    public Set<? extends User> getAll() {
        throw new Error("not implemented");
    }
}
