package it.unibo.ds.grpc.greeting;


import io.grpc.ServerBuilder;

import java.io.IOException;

public class Service  {
    public static void main(String[] args) throws IOException, InterruptedException {
        var server = ServerBuilder.forPort(10000)
                .addService(new MyGreetingService())
                .build();
        server.start();
        server.awaitTermination();
    }
}
