package it.unibo.ds.lab.sockets.server;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.ConnectException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class TestServer {

    public record TestProcess(Process process, File stdout, File stderr) {

    }

    @Test
    public void serverAloneSucceeds() throws IOException, InterruptedException {
        TestProcess client = startJavaProcess(EchoServer.class, 10001);
        assertFalse(client.process.waitFor(3, TimeUnit.SECONDS));
        client.process.destroy();
        String stderr = client.errorReader().lines().collect(Collectors.joining("\n"));
        String stdout = client.inputReader().lines().collect(Collectors.joining("\n"));
        assertTrue(client.waitFor(3, TimeUnit.SECONDS));
        System.out.println(stderr);
        System.out.println(stdout);
    }

    private TestProcess startJavaProcess(Class<?> klass, Object... args) throws IOException {
        Stream<String> command = Stream.of(
                new File(System.getProperty("java.home") + "/bin/java").getAbsolutePath(),
                "-classpath",
                System.getProperty("java.class.path"),
                klass.getName()
        );
        Stream<String> arguments = Stream.of(args).map(Object::toString);
        var commandLine = Stream.concat(command, arguments).collect(Collectors.toList());
        var prefix = TestServer.class.getName() + "-" + klass.getName() + "#" + Objects.hash(args);
        var stdOut = File.createTempFile(prefix, "stdout");
        var stdErr = File.createTempFile(prefix, "stdout");
        var process = new ProcessBuilder(commandLine).start();
        return new TestProcess(process, stdOut, stdErr);
    }
}
