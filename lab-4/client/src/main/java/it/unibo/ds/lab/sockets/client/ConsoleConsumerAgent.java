package it.unibo.ds.lab.sockets.client;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ConsoleConsumerAgent extends Thread {

    private static final int BUFFER_SIZE = 1024;
    private final byte[] buffer = new byte[BUFFER_SIZE];
    private final Socket server;

    public ConsoleConsumerAgent(Socket server) {
        this.server = server;
    }

    @Override
    public void run() {
        try {
            var inputStream = System.in;
            var outputStream = server.getOutputStream();
            while (true) {
                int readBytes = inputStream.read(buffer);
                if (readBytes < 0) {
                    System.out.println("Reached end of input");
                    break;
                } else {
                    outputStream.write(buffer, 0, readBytes);
                    System.out.printf("Sent %d bytes to %s\n", readBytes, server.getRemoteSocketAddress());
                    outputStream.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            try {
                server.shutdownOutput();
            } catch (IOException ignored) {
                // do nothing
            }
        }
    }
}
