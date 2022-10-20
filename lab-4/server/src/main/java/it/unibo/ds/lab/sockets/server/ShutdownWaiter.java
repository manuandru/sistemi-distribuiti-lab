package it.unibo.ds.lab.sockets.server;

import java.io.IOException;

public class ShutdownWaiter extends Thread {

    @Override
    public void run() {
        try {
            while (System.in.read() >= 0);
            System.out.println("Goodbye!");
            System.exit(0);
        } catch (IOException e) {
            System.exit(1);
        }
    }
}
