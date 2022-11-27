package it.unibo.ds.auth;

import com.google.protobuf.Empty;
import io.grpc.*;
import io.grpc.stub.StreamObserver;
import it.unibo.ds.auth.grpc.AuthenticatorGrpc;
import it.unibo.ds.auth.grpc.Proto;

import java.io.IOException;

public class AuthService extends AuthenticatorGrpc.AuthenticatorImplBase {
    private final Server service;
    private final Authenticator auth = new LocalAuthenticator();

    public AuthService(int port) {
        service = ServerBuilder.forPort(port)
                .addService(this)
                .build();
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

    private static Proto.Status statusOf(Proto.StatusCode code, String message) {
        return Proto.Status.newBuilder().setCode(code).setMessage(message).build();
    }

    private static Proto.Status ok() {
        return statusOf(Proto.StatusCode.OK, "Ok");
    }

    @Override
    public void register(Proto.User request, StreamObserver<Proto.EmptyResponse> responseObserver) {
        try {
            auth.register(Conversions.toJava(request));
            responseObserver.onNext(Proto.EmptyResponse.newBuilder().setStatus(ok()).build());
        } catch (ConflictException e) {
            responseObserver.onNext(
                    Proto.EmptyResponse.newBuilder().
                            setStatus(statusOf(Proto.StatusCode.CONFLICT, e.getMessage()))
                            .build()
            );
        } catch (IllegalArgumentException e) {
            responseObserver.onNext(
                    Proto.EmptyResponse.newBuilder().
                            setStatus(statusOf(Proto.StatusCode.BAD_CONTENT, e.getMessage()))
                            .build()
            );
        } catch (Throwable e) {
            responseObserver.onNext(
                    Proto.EmptyResponse.newBuilder().
                            setStatus(statusOf(Proto.StatusCode.GENERIC_ERROR, e.getMessage()))
                            .build()
            );
        }
        responseObserver.onCompleted();
    }

    @Override
    public void authorize(Proto.Credentials request, StreamObserver<Proto.TokenResponse> responseObserver) {
        try {
            var token = auth.authorize(Conversions.toJava(request));
            responseObserver.onNext(
                    Proto.TokenResponse.newBuilder()
                            .setStatus(ok())
                            .setToken(Conversions.toProto(token))
                            .build()
            );
        } catch (WrongCredentialsException e) {
            responseObserver.onNext(
                    Proto.TokenResponse.newBuilder().
                            setStatus(statusOf(Proto.StatusCode.WRONG_CREDENTIALS, e.getMessage()))
                            .build()
            );
        } catch (IllegalArgumentException e) {
            responseObserver.onNext(
                    Proto.TokenResponse.newBuilder().
                            setStatus(statusOf(Proto.StatusCode.BAD_CONTENT, e.getMessage()))
                            .build()
            );
        } catch (Throwable e) {
            responseObserver.onNext(
                    Proto.TokenResponse.newBuilder().
                            setStatus(statusOf(Proto.StatusCode.GENERIC_ERROR, e.getMessage()))
                            .build()
            );
        }
        responseObserver.onCompleted();
    }

    @Override
    public void remove(Proto.UserID request, StreamObserver<Proto.EmptyResponse> responseObserver) {
        try {
            auth.remove(request.getUsername());
            responseObserver.onNext(Proto.EmptyResponse.newBuilder().setStatus(ok()).build());
        } catch (MissingException e) {
            responseObserver.onNext(
                    Proto.EmptyResponse.newBuilder().
                            setStatus(statusOf(Proto.StatusCode.MISSING_CONTENT, e.getMessage()))
                            .build()
            );
        } catch (IllegalArgumentException e) {
            responseObserver.onNext(
                    Proto.EmptyResponse.newBuilder().
                            setStatus(statusOf(Proto.StatusCode.BAD_CONTENT, e.getMessage()))
                            .build()
            );
        } catch (Throwable e) {
            responseObserver.onNext(
                    Proto.EmptyResponse.newBuilder().
                            setStatus(statusOf(Proto.StatusCode.GENERIC_ERROR, e.getMessage()))
                            .build()
            );
        }
        responseObserver.onCompleted();
    }

    @Override
    public void get(Proto.UserID request, StreamObserver<Proto.UserResponse> responseObserver) {
        try {
            var user = auth.get(request.getUsername());
            System.out.println(user);
            responseObserver.onNext(
                    Proto.UserResponse.newBuilder()
                            .setStatus(ok())
                            .setUser(Conversions.toProto(user))
                            .build()
            );
        } catch (MissingException e) {
            responseObserver.onNext(
                    Proto.UserResponse.newBuilder().
                            setStatus(statusOf(Proto.StatusCode.MISSING_CONTENT, e.getMessage()))
                            .build()
            );
        } catch (IllegalArgumentException e) {
            responseObserver.onNext(
                    Proto.UserResponse.newBuilder().
                            setStatus(statusOf(Proto.StatusCode.BAD_CONTENT, e.getMessage()))
                            .build()
            );
        } catch (Throwable e) {
            responseObserver.onNext(
                    Proto.UserResponse.newBuilder().
                            setStatus(statusOf(Proto.StatusCode.GENERIC_ERROR, e.getMessage()))
                            .build()
            );
        }
        responseObserver.onCompleted();
    }

    @Override
    public void edit(Proto.EditRequest request, StreamObserver<Proto.EmptyResponse> responseObserver) {
        try {
            auth.edit(request.getUsername(), Conversions.toJava(request.getChanges()));
            responseObserver.onNext(Proto.EmptyResponse.newBuilder().setStatus(ok()).build());
        } catch (MissingException e) {
            responseObserver.onNext(
                    Proto.EmptyResponse.newBuilder().
                            setStatus(statusOf(Proto.StatusCode.MISSING_CONTENT, e.getMessage()))
                            .build()
            );
        }  catch (ConflictException e) {
            responseObserver.onNext(
                    Proto.EmptyResponse.newBuilder().
                            setStatus(statusOf(Proto.StatusCode.CONFLICT, e.getMessage()))
                            .build()
            );
        } catch (IllegalArgumentException e) {
            responseObserver.onNext(
                    Proto.EmptyResponse.newBuilder().
                            setStatus(statusOf(Proto.StatusCode.BAD_CONTENT, e.getMessage()))
                            .build()
            );
        } catch (Throwable e) {
            responseObserver.onNext(
                    Proto.EmptyResponse.newBuilder().
                            setStatus(statusOf(Proto.StatusCode.GENERIC_ERROR, e.getMessage()))
                            .build()
            );
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getAll(Empty request, StreamObserver<Proto.User> responseObserver) {
        try {
            for (var user : auth.getAll()) {
                responseObserver.onNext(Conversions.toProto(user));
            }
            responseObserver.onCompleted();
        } catch (Throwable e) {
            responseObserver.onError(new StatusRuntimeException(Status.INTERNAL));
        }
    }
}
