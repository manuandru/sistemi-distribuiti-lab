package it.unibo.ds.lab.sockets.server;

import it.unibo.ds.lab.sockets.BaseTest;
import it.unibo.ds.lab.sockets.TestableProcess;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class TestServer extends BaseTest {

    @Test
    public void serverAloneListens() throws IOException, InterruptedException {
        try (TestableProcess server = startJavaProcess(EchoServer.class, port)) {
            assertFalse(server.process().waitFor(3, TimeUnit.SECONDS));
            server.process().destroy();
            assertTrue(server.process().waitFor(3, TimeUnit.SECONDS));
            assertTrue(server.stderrAsText().isBlank());
            assertEquals(
                    "Bound to port %d\nGoodbye!".formatted(port),
                    server.stdoutAsText()
            );
        }
    }

    @Test
    public void serverStopsGracefullyWhenClosingStdin() throws IOException, InterruptedException {
        try (TestableProcess server = startJavaProcess(EchoServer.class, port)) {
            server.stdin().close();
            assertTrue(server.process().waitFor(3, TimeUnit.SECONDS));
            assertTrue(server.stderrAsText().isBlank());
            assertEquals(
                    "Bound to port %d\nGoodbye!".formatted(port),
                    server.stdoutAsText()
            );
        }
    }
}
