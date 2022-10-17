package it.unibo.ds.lab.sockets.server;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ServerSideEchoerAgent extends Thread {

    private static final int BUFFER_SIZE = 1024;
    private final byte[] buffer = new byte[BUFFER_SIZE];
    private final Socket client;

    public ServerSideEchoerAgent(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            var inputStream = client.getInputStream();
            var outputStream = client.getOutputStream();
            while (true) {
                int readBytes = inputStream.read(buffer);
                if (readBytes < 0) {
                    client.shutdownOutput();
                    break;
                } else {
                    System.out.printf("Echoed %d bytes from %s\n", readBytes, client.getRemoteSocketAddress());
                    outputStream.write(buffer, 0, readBytes);
                    outputStream.flush();
                }
            }
        } catch (SocketException e) {
            // silently ignores
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (!client.isClosed()) {
                    client.close();
                }
            } catch (IOException ignored) {
                // silently ignores
            }
        }
    }
}
