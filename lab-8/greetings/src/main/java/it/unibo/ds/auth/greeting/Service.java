package it.unibo.ds.auth.greeting;


import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class Service  {

    private static Server service;

    public static void main(String[] args) throws IOException, InterruptedException {
        var port = Integer.parseInt(args.length > 0 ? args[0] : "10000");
        start(port);
        System.out.println("Listening on port " + port);
        service.awaitTermination();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> service.shutdownNow()));
    }

    public static void start(int port) throws IOException {
        service = ServerBuilder.forPort(port)
                .addService(new MyGreetingService())
                .build();
        service.start();
    }

    public static void stop() throws InterruptedException {
        service.shutdown();
        service.awaitTermination();
    }
}
