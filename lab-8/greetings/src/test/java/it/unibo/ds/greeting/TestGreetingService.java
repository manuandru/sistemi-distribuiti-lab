package it.unibo.ds.greeting;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.internal.testing.StreamRecorder;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TestGreetingService {

    private ManagedChannel channel;
    private Server server;

    @BeforeEach
    public void beforeEach() throws IOException {
        server = InProcessServerBuilder.forName("greeting").addService(new MyGreetingService()).build();
        channel = InProcessChannelBuilder.forName("greeting").usePlaintext().build();
        server.start();
    }

    @AfterEach
    public void afterEach() throws InterruptedException {
        channel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
        server.shutdown().awaitTermination();
    }

    private static HelloRequest helloRequest(String name) {
        return HelloRequest.newBuilder().setName(name).build();
    }

    private static ArrayOfHelloRequests arrayOfHelloRequests(String... names) {
        var builder = ArrayOfHelloRequests.newBuilder();
        for (var name : names) {
            builder.addItems(helloRequest(name));
        }
        return builder.build();
    }

    private static HelloReply helloResponse(String message) {
        return HelloReply.newBuilder().setMessage("Hello " + message + "!").build();
    }

    ///////////////////////////////////////////////////////////////////////////
    // SayHello
    ///////////////////////////////////////////////////////////////////////////

    @Test
    public void testBlockingSayHello() {
        var client = GreeterGrpc.newBlockingStub(channel);

        HelloReply reply = client.sayHello(helloRequest("Giovanni"));

        assertEquals(helloResponse("Giovanni"), reply);
    }

    @Test
    public void testAsyncSayHello() throws ExecutionException, InterruptedException {
        var client = GreeterGrpc.newFutureStub(channel);

        Future<HelloReply> reply = client.sayHello(helloRequest("Giovanni"));

        assertEquals(helloResponse("Giovanni"), reply.get());
    }

    @Test
    public void testSayHello() throws Exception {
        var client = GreeterGrpc.newStub(channel);

        StreamRecorder<HelloReply> responseStream = StreamRecorder.create();
        client.sayHello(helloRequest("Giovanni"), responseStream);

        responseStream.awaitCompletion();
        assertEquals(
                List.of(
                        helloResponse("Giovanni")
                ),
                responseStream.getValues()
        );
    }

    ///////////////////////////////////////////////////////////////////////////
    // SayHelloToBunch
    ///////////////////////////////////////////////////////////////////////////

    @Test
    public void testBlockingSayHelloToBunch() {
        var client = GreeterGrpc.newBlockingStub(channel);

        HelloReply reply = client.sayHelloToBunch(arrayOfHelloRequests("Giovanni", "Andrea", "Stefano"));

        assertEquals(
                helloResponse("Giovanni, Andrea, Stefano"),
                reply
        );
    }

    @Test
    public void testAsyncSayHelloToBunch() throws ExecutionException, InterruptedException {
        var client = GreeterGrpc.newFutureStub(channel);

        Future<HelloReply> reply = client.sayHelloToBunch(arrayOfHelloRequests("Giovanni", "Andrea", "Stefano"));

        assertEquals(
                helloResponse("Giovanni, Andrea, Stefano"),
                reply.get()
        );
    }

    @Test
    public void testSayHelloToBunch() throws Exception {
        var client = GreeterGrpc.newStub(channel);

        StreamRecorder<HelloReply> responseStream = StreamRecorder.create();
        client.sayHelloToBunch(arrayOfHelloRequests("Giovanni", "Andrea", "Stefano"), responseStream);

        responseStream.awaitCompletion();
        assertEquals(
                List.of(
                        helloResponse("Giovanni, Andrea, Stefano")
                ),
                responseStream.getValues()
        );
    }

    ///////////////////////////////////////////////////////////////////////////
    // SayHelloToBunchAsStream
    ///////////////////////////////////////////////////////////////////////////

    @Test
    public void testBlockingSayHelloToBunchAsStream() {
        var client = GreeterGrpc.newBlockingStub(channel);

        Iterator<HelloReply> reply = client.sayHelloToBunchAsStream(arrayOfHelloRequests("Giovanni", "Andrea", "Stefano"));

        assertEquals(helloResponse("Giovanni"), reply.next());
        assertEquals(helloResponse("Andrea"), reply.next());
        assertEquals(helloResponse("Stefano"), reply.next());
        assertFalse(reply.hasNext());
    }

    // no async stub //////////////////////////////////////////////////////////

    @Test
    public void testSayHelloToBunchAsStream() throws Exception {
        var client = GreeterGrpc.newStub(channel);

        StreamRecorder<HelloReply> responseStream = StreamRecorder.create();
        client.sayHelloToBunchAsStream(arrayOfHelloRequests("Giovanni", "Andrea", "Stefano"), responseStream);

        responseStream.awaitCompletion();
        assertEquals(
                List.of(
                        helloResponse("Giovanni"),
                        helloResponse("Andrea"),
                        helloResponse("Stefano")
                ),
                responseStream.getValues()
        );
    }

    ///////////////////////////////////////////////////////////////////////////
    // SayHalloToMany
    ///////////////////////////////////////////////////////////////////////////

    // no blocking stub ///////////////////////////////////////////////////////
    // no async stub //////////////////////////////////////////////////////////

    @Test
    public void testSayHelloToMany() throws Exception {
        var client = GreeterGrpc.newStub(channel);

        StreamRecorder<HelloReply> responseStream = StreamRecorder.create();
        StreamObserver<HelloRequest> request = client.sayHelloToMany(responseStream);

        request.onNext(helloRequest("Giovanni"));
        request.onNext(helloRequest("Andrea"));
        request.onNext(helloRequest("Stefano"));
        request.onCompleted();

        responseStream.awaitCompletion();
        assertEquals(
                List.of(
                        helloResponse("Giovanni, Andrea, Stefano")
                ),
                responseStream.getValues()
        );
    }

    ///////////////////////////////////////////////////////////////////////////
    // SayHalloToMany
    ///////////////////////////////////////////////////////////////////////////

    // no blocking stub ///////////////////////////////////////////////////////
    // no async stub //////////////////////////////////////////////////////////

    @Test
    public void testSayHelloToManyAsStream() throws Exception {
        var client = GreeterGrpc.newStub(channel);

        StreamRecorder<HelloReply> responseStream = StreamRecorder.create();
        StreamObserver<HelloRequest> request = client.sayHelloToManyAsStream(responseStream);

        request.onNext(helloRequest("Giovanni"));
        request.onNext(helloRequest("Andrea"));
        request.onNext(helloRequest("Stefano"));
        request.onCompleted();

        responseStream.awaitCompletion();
        assertEquals(
                List.of(
                        helloResponse("Giovanni"),
                        helloResponse("Andrea"),
                        helloResponse("Stefano")
                ),
                responseStream.getValues()
        );
    }

}
