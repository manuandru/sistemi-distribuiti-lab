package it.unibo.ds.auth.greeting;

import io.grpc.stub.StreamObserver;

import java.util.stream.Collectors;

public class MyGreetingService extends GreeterGrpc.GreeterImplBase {
    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        responseObserver.onNext(greet(request));
        responseObserver.onCompleted();
    }

    private HelloReply greet(String name) {
        return HelloReply.newBuilder().setMessage("Hello " + name + "!").build();
    }

    private HelloReply greet(HelloRequest request) {
        return greet(request.getName());
    }

    private HelloReply greet(ArrayOfHelloRequests request) {
        var message = request.getItemsList().stream()
                .map(HelloRequest::getName)
                .collect(Collectors.joining(", "));
        return greet(message);
    }

    @Override
    public void sayHelloToBunch(ArrayOfHelloRequests request, StreamObserver<HelloReply> responseObserver) {
        responseObserver.onNext(greet(request));
        responseObserver.onCompleted();
    }

    @Override
    public void sayHelloToBunchAsStream(ArrayOfHelloRequests request, StreamObserver<HelloReply> responseObserver) {
        for (int i = 0; i < request.getItemsCount(); i++) {
            responseObserver.onNext(greet(request.getItems(i)));
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<HelloRequest> sayHelloToMany(StreamObserver<HelloReply> responseObserver) {
        return new StreamObserver<>() {
            String message = null;
            int index = 0;

            @Override
            public void onNext(HelloRequest value) {
                if (index++ == 0) {
                    message = value.getName();
                } else {
                    message += ", " + value.getName();
                }
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(greet(message));
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<HelloRequest> sayHelloToManyAsStream(StreamObserver<HelloReply> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(HelloRequest value) {
                responseObserver.onNext(greet(value));
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}
