package it.unibo.ds.lab.sockets.server;

import java.io.IOException;

public class TerminationWaiterAgent extends Thread {

    @Override
    public void run() {
        try {
            while (System.in.read() >= 0) {
                // does nothing, just consumes one byte
            }
            System.out.println("Goodbye!");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
