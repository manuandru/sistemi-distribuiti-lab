package it.unibo.ds.lab.sockets.client;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientSideEchoerAgent extends Thread {

    private static final int BUFFER_SIZE = 1024;
    private final Socket server;

    public ClientSideEchoerAgent(Socket server) {
        this.server = server;
    }

    @Override
    public void run() {
        try {
            var inputStream = new BufferedInputStream(server.getInputStream());
            var outputStream = System.out;
            while (true) {
                byte[] data = inputStream.readNBytes(BUFFER_SIZE);
                if (data.length == 0) {
                    outputStream.close();
                    return;
                } else {
                    outputStream.write(data);
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
