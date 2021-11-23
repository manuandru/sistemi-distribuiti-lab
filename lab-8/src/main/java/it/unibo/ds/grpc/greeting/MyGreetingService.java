package it.unibo.ds.grpc.greeting;

import io.grpc.stub.StreamObserver;

public class MyGreetingService extends GreeterGrpc.GreeterImplBase {
    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        var message = String.format("Hello %s!", request.getName());
        responseObserver.onNext(HelloReply.newBuilder().setMessage(message).build());
        responseObserver.onCompleted();
    }
}
