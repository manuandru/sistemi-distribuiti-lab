package it.unibo.ds.grpc.greeting;

import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Client {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        var channel = ManagedChannelBuilder.forAddress("localhost", 10000)
                .usePlaintext()
                .build();

        var client = GreeterGrpc.newBlockingStub(channel);
        var asyncClient = GreeterGrpc.newFutureStub(channel);

        var giovanni = HelloRequest.newBuilder().setName("Giovanni").build();
        var andrea = HelloRequest.newBuilder().setName("Andrea").build();

        ListenableFuture<HelloReply> asyncReply = asyncClient.sayHello(giovanni);
        HelloReply reply = client.sayHello(andrea);

        System.out.println(asyncReply);
        System.out.println(reply.getMessage());
        System.out.println(asyncReply.get().getMessage());

        channel.shutdown();
        channel.awaitTermination(1, TimeUnit.SECONDS);
    }
}
