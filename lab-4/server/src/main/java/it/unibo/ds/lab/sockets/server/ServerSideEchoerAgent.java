package it.unibo.ds.lab.sockets.server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;

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
            var inputStream = new BufferedInputStream(client.getInputStream());
            var outputStream = client.getOutputStream();
            while (true) {
                int readBytes = inputStream.read(buffer);
                if (readBytes < 0) {
                    outputStream.close();
                    return;
                } else {
                    System.out.printf("Echoed %d bytes\n", readBytes);
                    outputStream.write(readBytes);
                    outputStream.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (client.isClosed()) {
                try {
                    client.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
