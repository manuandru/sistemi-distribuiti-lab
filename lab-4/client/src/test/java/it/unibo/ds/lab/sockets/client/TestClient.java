package it.unibo.ds.lab.sockets.client;

import it.unibo.ds.lab.sockets.BaseTest;
import it.unibo.ds.lab.sockets.TestableProcess;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestClient extends BaseTest {
    @Test
    public void clientAloneFails() throws IOException, InterruptedException {
        try (TestableProcess client = startJavaProcess(EchoClient.class, "localhost", port)) {
            assertTrue(client.process().waitFor(3, TimeUnit.SECONDS));
            assertEquals(1, client.process().exitValue());
            assertTrue(client.stderrAsText().contains(ConnectException.class.getName()));
            assertTrue(client.stderrAsText().contains(EchoClient.class.getName()));
            assertEquals(
                    "Contacting host localhost:%d...".formatted(port),
                    client.stdoutAsText()
            );
        }
    }
}
