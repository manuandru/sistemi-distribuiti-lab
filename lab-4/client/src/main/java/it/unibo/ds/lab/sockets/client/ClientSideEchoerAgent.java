package it.unibo.ds.lab.sockets.client;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

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
                    outputStream.close();
                    return;
                } else {
                    outputStream.write(buffer, 0, readBytes);
                    outputStream.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (server.isClosed()) {
                try {
                    server.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
