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
                    outputStream.close();
                    break;
                } else {
                    outputStream.write(buffer, 0, readBytes);
                    outputStream.flush();
                }
            }
        } catch (SocketException ignored) {
            System.err.println("Connection lost");
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            try {
                if (!server.isClosed()) {
                    server.close();
                }
            } catch (IOException ignored) {
                // silently ignores
            }
        }
    }
}
