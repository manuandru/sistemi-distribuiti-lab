package it.unibo.ds.grpc.greeting;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.internal.testing.StreamRecorder;
import io.grpc.stub.StreamObserver;
import io.grpc.stub.StreamObservers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class TestClient {

    private ManagedChannel channel;

    @Before
    public void beforeEach() throws IOException {
        Service.start(10000);
        channel = ManagedChannelBuilder.forAddress("localhost", 10000)
                .usePlaintext()
                .build();
    }

    @After
    public void afterEach() throws InterruptedException {
        channel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
        Service.stop();
    }

    private static HelloRequest request(String name) {
        return HelloRequest.newBuilder().setName(name).build();
    }

    private static ArrayOfHelloRequests requests(String... names) {
        var builder = ArrayOfHelloRequests.newBuilder();
        for (var name : names) {
            builder.addItems(request(name));
        }
        return builder.build();
    }

    private static HelloReply reply(String message) {
        return HelloReply.newBuilder().setMessage("Hello " + message + "!").build();
    }

    @Test
    public void testBlockingSayHello() {
        var client = GreeterGrpc.newBlockingStub(channel);
        HelloReply reply = client.sayHello(request("Giovanni"));
        Assert.assertEquals(reply("Giovanni"), reply);
    }

    @Test
    public void testAsyncSayHello() throws ExecutionException, InterruptedException {
        var client = GreeterGrpc.newFutureStub(channel);
        Future<HelloReply> reply = client.sayHello(request("Giovanni"));
        Assert.assertEquals(reply("Giovanni"), reply.get());
    }

    @Test
    public void testSayHello() {
        var client = GreeterGrpc.newStub(channel);
        StreamRecorder<HelloReply> responseStream = StreamRecorder.create();
        client.sayHello(request("Giovanni"), responseStream);
        Assert.assertEquals(
                List.of(reply("Giovanni")),
                responseStream.getValues()
        );
    }

    @Test
    public void testBlockingSayHelloSayHelloToBunch() {

    }

    @Test
    public void testSayHelloSayHelloToBunch() {

    }

    @Test
    public void testAsyncSayHelloSayHelloToBunch() {

    }

}
