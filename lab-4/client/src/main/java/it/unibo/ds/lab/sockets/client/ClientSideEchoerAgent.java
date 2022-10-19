package it.unibo.ds.lab.sockets.client;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ClientSideEchoerAgent extends Thread {

    private static final int BUFFER_SIZE = 1024;
    private final byte[] buffer = new byte[BUFFER_SIZE];
    private final Socket server;

    public ClientSideEchoerAgent(Socket server) {
        this.server = server;
    }

    @Override
    public void run() {
        try {
            var inputStream = server.getInputStream();
            var outputStream = System.out;
            while (true) {
                int readBytes = inputStream.read(buffer);
                if (readBytes < 0) {
                    System.out.printf("Received EOF from %s\n", server.getRemoteSocketAddress());
                    break;
                } else {
                    System.out.printf("Received %d bytes from %s\n", readBytes, server.getRemoteSocketAddress());
                    outputStream.write(buffer, 0, readBytes);
                    outputStream.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
