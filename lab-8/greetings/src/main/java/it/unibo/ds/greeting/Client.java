package it.unibo.ds.greeting;

import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Client {

    private static final HelloRequest giovanni = HelloRequest.newBuilder().setName("Giovanni").build();
    private static final HelloRequest andrea = HelloRequest.newBuilder().setName("Andrea").build();
    private static final HelloRequest stefano = HelloRequest.newBuilder().setName("Stefano").build();


    private static final StreamObserver<HelloReply> replyPrinter = new StreamObserver<>() {

        @Override
        public void onNext(HelloReply value) {
            System.out.println(value.getMessage());
        }

        @Override
        public void onError(Throwable t) {
            t.printStackTrace();
        }

        @Override
        public void onCompleted() {
            // do nothing
        }
    };

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        var channel = ManagedChannelBuilder.forAddress("localhost", 10000)
                .usePlaintext()
                .build();

        var blockingClient = GreeterGrpc.newBlockingStub(channel);
        var asyncClient = GreeterGrpc.newFutureStub(channel);
        var client = GreeterGrpc.newStub(channel);

        ListenableFuture<HelloReply> asyncReply = asyncClient.sayHello(giovanni);
        HelloReply reply = blockingClient.sayHello(andrea);
        client.sayHello(stefano, replyPrinter);

        System.out.println(asyncReply);
        System.out.println(reply.getMessage());
        System.out.println(asyncReply.get().getMessage());

        var request = client.sayHelloToMany(replyPrinter);
        request.onNext(giovanni);
        request.onNext(andrea);
        request.onNext(stefano);
        request.onCompleted();

        channel.shutdown();
        channel.awaitTermination(1, TimeUnit.SECONDS);
    }
}
