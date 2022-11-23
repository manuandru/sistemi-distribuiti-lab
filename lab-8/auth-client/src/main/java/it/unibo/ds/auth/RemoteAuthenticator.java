package it.unibo.ds.auth;

import com.google.protobuf.Empty;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import it.unibo.ds.auth.grpc.AuthenticatorGrpc;
import it.unibo.ds.auth.grpc.Proto;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RemoteAuthenticator implements Authenticator {
    private final AuthenticatorGrpc.AuthenticatorBlockingStub blockingClient;
    private final AuthenticatorGrpc.AuthenticatorStub client;

    public RemoteAuthenticator(String hostname, int port) {
        var channel = ManagedChannelBuilder.forAddress(hostname, port)
                .usePlaintext()
                .build();
        blockingClient = AuthenticatorGrpc.newBlockingStub(channel);
        client = AuthenticatorGrpc.newStub(channel);
    }


    @Override
    public void register(User user) throws ConflictException {
        var response = blockingClient.register(Conversions.toProto(user));
        switch (response.getStatus().getCode()) {
            case OK -> { }
            case CONFLICT -> throw new ConflictException(response.getStatus().getMessage());
            case GENERIC_ERROR -> throw new IllegalStateException(response.getStatus().getMessage());
            case BAD_CONTENT -> throw new IllegalArgumentException(response.getStatus().getMessage());
            default -> throw new Error("Unexpected behaviour of server");
        }
    }

    @Override
    public Token authorize(Credentials credentials) throws WrongCredentialsException {
        var response = blockingClient.authorize(Conversions.toProto(credentials));
        switch (response.getStatus().getCode()) {
            case OK -> {
                return Conversions.toJava(response.getToken());
            }
            case WRONG_CREDENTIALS -> throw new WrongCredentialsException(response.getStatus().getMessage());
            case GENERIC_ERROR -> throw new IllegalStateException(response.getStatus().getMessage());
            case BAD_CONTENT -> throw new IllegalArgumentException(response.getStatus().getMessage());
            default -> throw new Error("Unexpected behaviour of server");
        }
    }

    @Override
    public void remove(String userId) throws MissingException {
        var response = blockingClient.remove(Proto.UserID.newBuilder().setId(userId).build());
        switch (response.getStatus().getCode()) {
            case OK -> { }
            case NOT_FOUND -> throw new MissingException(response.getStatus().getMessage());
            case GENERIC_ERROR -> throw new IllegalStateException(response.getStatus().getMessage());
            case BAD_CONTENT -> throw new IllegalArgumentException(response.getStatus().getMessage());
            default -> throw new Error("Unexpected behaviour of server");
        }
    }

    @Override
    public User get(String userId) throws MissingException {
        var response = blockingClient.get(Proto.UserID.newBuilder().setId(userId).build());
        switch (response.getStatus().getCode()) {
            case OK -> {
                return Conversions.toJava(response.getUser());
            }
            case NOT_FOUND -> throw new MissingException(response.getStatus().getMessage());
            case GENERIC_ERROR -> throw new IllegalStateException(response.getStatus().getMessage());
            case BAD_CONTENT -> throw new IllegalArgumentException(response.getStatus().getMessage());
            default -> throw new Error("Unexpected behaviour of server");
        }
    }

    @Override
    public void edit(String userId, User changes) throws MissingException, ConflictException {
        var response = blockingClient.edit(
                Proto.EditRequest.newBuilder().setUserID(userId).setChanges(Conversions.toProto(changes)).build()
        );
        switch (response.getStatus().getCode()) {
            case OK -> { }
            case NOT_FOUND -> throw new MissingException(response.getStatus().getMessage());
            case CONFLICT -> throw new ConflictException(response.getStatus().getMessage());
            case GENERIC_ERROR -> throw new IllegalStateException(response.getStatus().getMessage());
            case BAD_CONTENT -> throw new IllegalArgumentException(response.getStatus().getMessage());
            default -> throw new Error("Unexpected behaviour of server");
        }
    }

    private class UserStreamCollector implements StreamObserver<Proto.User> {

        private final CompletableFuture<Set<User>> resultPromise;
        private final Set<User> result = Collections.synchronizedSet(new HashSet<>());

        private UserStreamCollector(CompletableFuture<Set<User>> resultPromise) {
            this.resultPromise = resultPromise;
        }

        @Override
        public void onNext(Proto.User value) {
            result.add(Conversions.toJava(value));
        }

        @Override
        public void onError(Throwable t) {
            resultPromise.completeExceptionally(t);
        }

        @Override
        public void onCompleted() {
            resultPromise.complete(result);
        }
    }

    @Override
    public Set<? extends User> getAll() {
        final CompletableFuture<Set<User>> resultPromise = new CompletableFuture<>();
        client.getAll(Empty.getDefaultInstance(), new UserStreamCollector(resultPromise));
        try {
            return resultPromise.get();
        } catch (InterruptedException | ExecutionException e) {
            if (e.getCause() instanceof RuntimeException re) {
                throw re;
            } else if (e.getCause() == null) {
                throw new RuntimeException(e);
            } else {
                throw new RuntimeException(e.getCause());
            }
        }
    }
}
