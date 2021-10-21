/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package it.unibo.ds.lab.sockets.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class EchoClient {

    public static void main(String[] args) {
        var host = args[0];
        var port = Integer.parseInt(args[1]);
        try {
            echo(host, port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (InterruptedException e) {
            // silently ignored
        }
    }

    public static void echo(String host, int port) throws IOException, InterruptedException {
        Socket server = new Socket();

        System.out.printf("Contacting host %s:%d...\n", host, port);
        // connect to the host, possibly with timeout
        System.out.println("Connection established");

        // TODO:
        // - read bytes from stdin and redirect them to the socket's output stream
        // - read bytes from the socket's input stream and redirect them to stdout
        echoImpl(server);

        System.out.println("Goodbye!");
    }

    private static final int BUFFER_SIZE = 1024;

    private static void echoImpl(Socket server) {
        var buffer = new byte[BUFFER_SIZE];
        try (server) {
            propagateStdinToServer(server, buffer);
            propagateServerToStdout(server, buffer);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void propagateStdinToServer(Socket server, byte[] buffer) throws IOException {
        // TODO: read bytes from stdin and redirect them to the socket's output stream
    }

    private static void propagateServerToStdout(Socket server, byte[] buffer) throws IOException {
        // TODO read bytes from the socket's input stream and redirect them to stdout
    }

}
