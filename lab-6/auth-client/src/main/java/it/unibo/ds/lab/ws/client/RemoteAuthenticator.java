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
        var request = HttpRequest.newBuilder()
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
        var request = HttpRequest.newBuilder()
                .uri(resourceUri("/users"))
                .header("Accept", "application/json")
                .POST(body(user))
                .build();
        return sendRequestToClient(request)
                .thenComposeAsync(checkResponse());
    }

    @Override
    public void register(User user) throws ConflictException {
        try {
            registerAsync(user).join();
        } catch (CompletionException e) {
            throw getCauseAs(e, ConflictException.class);
        }
    }

    private CompletableFuture<?> removeAsync(String userId) {
        var request = HttpRequest.newBuilder()
                .uri(resourceUri("/users/" + userId))
                .DELETE()
                .build();
        return sendRequestToClient(request)
                .thenComposeAsync(checkResponse());
    }

    @Override
    public void remove(String userId) throws MissingException {
        try {
            removeAsync(userId).join();
        } catch (CompletionException e) {
            throw getCauseAs(e, MissingException.class);
        }
    }

    private CompletableFuture<User> getAsync(String userId) {
        var request = HttpRequest.newBuilder()
                .uri(resourceUri("/users/" + userId))
                .header("Accept", "application/json")
                .GET()
                .build();
        return sendRequestToClient(request)
                .thenComposeAsync(checkResponse())
                .thenComposeAsync(deserializeOne(User.class));
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
        var request = HttpRequest.newBuilder()
                .uri(resourceUri("/users/" + userId))
                .header("Accept", "application/json")
                .PUT(body(changes))
                .build();
        return sendRequestToClient(request)
                .thenComposeAsync(checkResponse());
    }

    @Override
    public void edit(String userId, User changes) throws MissingException, ConflictException {
        try {
            editAsync(userId, changes).join();
        } catch (CompletionException e) {
            if (e.getCause() instanceof MissingException) {
                throw (MissingException) e.getCause();
            } else if (e.getCause() instanceof ConflictException) {
                throw (ConflictException) e.getCause();
            } else if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            } else {
                throw e;
            }
        }
    }

    protected CompletableFuture<List<String>> getAllNamesAsync() {
        var request = HttpRequest.newBuilder()
                .uri(resourceUriWithQuery("/users", "limit", Integer.MAX_VALUE))
                .header("Accept", "application/json")
                .GET()
                .build();
        return sendRequestToClient(request)
                .thenComposeAsync(checkResponse())
                .thenComposeAsync(deserializeMany(String.class));
    }

    @Override
    public Set<? extends User> getAll() {
        try {
            var futureUsers = getAllNamesAsync()
                    .thenApplyAsync(names -> names.stream().map(this::getAsync).collect(Collectors.toList()))
                    .join();
            var users = new HashSet<User>();
            for (var futureUser : futureUsers) {
                users.add(futureUser.join());
            }
            return users;
        } catch (CompletionException e) {
            throw getCauseAs(e, RuntimeException.class);
        }
    }
}
