/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package it.unibo.ds.lab.sockets.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        try {
            listen(port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void listen(int port) throws IOException {
        var server = new ServerSocket();

        server.bind(new InetSocketAddress(port));

        var terminationWaiter = new TerminationWaiterAgent();
        terminationWaiter.start();

        while (!server.isClosed()) {
            System.out.printf("Listening on port %d\n", port);
            Socket client = server.accept();
            System.out.printf("Accepted connection from: %s\n", client.getRemoteSocketAddress());
            var echoer = new ServerSideEchoerAgent(client);
            echoer.start();
        }
    }
}
