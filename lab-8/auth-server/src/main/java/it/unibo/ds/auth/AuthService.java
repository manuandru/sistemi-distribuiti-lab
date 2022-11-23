package it.unibo.ds.auth;

import com.google.protobuf.Empty;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import it.unibo.ds.auth.grpc.AuthenticatorGrpc;
import it.unibo.ds.auth.grpc.Proto;

import java.io.IOException;

public class AuthService extends AuthenticatorGrpc.AuthenticatorImplBase{
    private final Server service;
    private final Authenticator auth = new LocalAuthenticator();

    public AuthService(int port) {
        service = ServerBuilder.forPort(port)
                .addService(this)
                .build();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        var port = Integer.parseInt(args.length > 0 ? args[0] : "10000");
        System.out.println("Listening on port " + port);
        var service = new AuthService(port);
        service.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> service.stop()));
        service.join();
    }

    public void start() throws IOException {
        service.start();
    }

    public void stop() {
        service.shutdown();
    }

    public void join() throws InterruptedException {
        service.awaitTermination();
    }

    @Override
    public void register(Proto.User request, StreamObserver<Proto.UserResponse> responseObserver) {
        super.register(request, responseObserver);
    }

    @Override
    public void authorize(Proto.Credentials request, StreamObserver<Proto.TokenResponse> responseObserver) {
        super.authorize(request, responseObserver);
    }

    @Override
    public void remove(Proto.UserID request, StreamObserver<Proto.EmptyResponse> responseObserver) {
        super.remove(request, responseObserver);
    }

    @Override
    public void get(Proto.UserID request, StreamObserver<Proto.UserResponse> responseObserver) {
        super.get(request, responseObserver);
    }

    @Override
    public void edit(Proto.EditRequest request, StreamObserver<Proto.EmptyResponse> responseObserver) {
        super.edit(request, responseObserver);
    }

    @Override
    public void getAll(Empty request, StreamObserver<Proto.User> responseObserver) {
        super.getAll(request, responseObserver);
    }
}
