package it.unibo.ds.lab.sockets;

import org.opentest4j.AssertionFailedError;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BaseTest {

    protected final int port;

    public BaseTest() {
        port = new Random().nextInt(10000, 20000);
    }

    protected static void assertMatches(String expectedRegex, String actual) {
        if (!actual.matches(expectedRegex)) {
            throw new AssertionFailedError(
                    "expected something matching: <%s> but was: <%s>".formatted(
                            escapeBlank(expectedRegex),
                            escapeBlank(actual)
                    ),
                    expectedRegex,
                    actual
            );
        }
    }

    private static String escapeBlank(String string) {
        return string.replace("\n", "\\n").replace("\r", "\\r");
    }

    protected TestableProcess startJavaProcess(Class<?> klass, Object... args) throws IOException {
        Stream<String> command = Stream.of(
                new File(System.getProperty("java.home") + "/bin/java").getAbsolutePath(),
                "-classpath",
                System.getProperty("java.class.path"),
                klass.getName()
        );
        Stream<String> arguments = Stream.of(args).map(Object::toString);
        var commandLine = Stream.concat(command, arguments).collect(Collectors.toList());
        var prefix = this.getClass().getName() + "-" + klass.getName() + "#" + Objects.hash(args);
        var stdOut = File.createTempFile(prefix + "-stdout", ".txt");
        stdOut.deleteOnExit();
        var stdErr = File.createTempFile(prefix + "-stderr", ".txt");
        stdErr.deleteOnExit();
        var process = new ProcessBuilder(commandLine)
                .redirectOutput(ProcessBuilder.Redirect.to(stdOut))
                .redirectError(ProcessBuilder.Redirect.to(stdErr))
                .start();
        return new TestableProcess(process, stdOut, stdErr);
    }
}
