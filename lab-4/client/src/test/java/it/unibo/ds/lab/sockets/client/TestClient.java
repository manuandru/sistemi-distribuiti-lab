package it.unibo.ds.lab.sockets.client;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestClient {
    @Test
    public void clientAloneFails() throws IOException, InterruptedException {
        Process client = startJavaProcess(EchoClient.class, "localhost", 10000);
        String stderr = client.errorReader().lines().collect(Collectors.joining("\n"));
        assertTrue(client.waitFor(3, TimeUnit.SECONDS));
        assertEquals(1, client.exitValue());
        assertTrue(stderr.contains(ConnectException.class.getName()));
        assertTrue(stderr.contains(EchoClient.class.getName()));
    }

    private Process startJavaProcess(Class<?> klass, Object... args) throws IOException {
        Stream<String> command = Stream.of(
                new File(System.getProperty("java.home") + "/bin/java").getAbsolutePath(),
                "-classpath",
                System.getProperty("java.class.path"),
                klass.getName()
        );
        Stream<String> arguments = Stream.of(args).map(Object::toString);
        return new ProcessBuilder(
                Stream.concat(command, arguments).collect(Collectors.toList())
        ).start();
    }
}
