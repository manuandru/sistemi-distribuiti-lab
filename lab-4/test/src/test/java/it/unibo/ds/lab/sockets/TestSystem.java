package it.unibo.ds.lab.sockets;

import it.unibo.ds.lab.sockets.client.EchoClient;
import it.unibo.ds.lab.sockets.server.EchoServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class TestSystem extends BaseTest {

    @Test
    public void emptyInputStreamIsEchoedAsWell() throws IOException, InterruptedException {
        try (TestableProcess server = startJavaProcess(EchoServer.class, port)) {
            try (TestableProcess client = startJavaProcess(EchoClient.class, "localhost", port)) {
                client.stdin().close();
                assertTrue(client.process().waitFor(3, TimeUnit.SECONDS));
                assertEquals(0, client.process().exitValue());
                client.printDebugInfo("client");
                assertTrue(client.stderrAsText().isBlank());
                assertMatches(
                        """
                        Contacting host localhost:$serverPort...
                        Connection established
                        Reached end of input
                        Received EOF from localhost/127.0.0.1:$serverPort
                        Goodbye!""".replace("$serverPort", Integer.toString(port)),
                        client.stdoutAsText()
                );
            }
            server.stdin().close();
            assertTrue(server.process().waitFor(3, TimeUnit.SECONDS));
            assertEquals(0, server.process().exitValue());
            server.printDebugInfo("server");
            assertTrue(server.stderrAsText().isBlank());
            assertMatches(
                    """
                    Bound to port $serverPort
                    Accepted connection from: /127.0.0.1:(?<clientPort>\\d+), on local port $serverPort
                    End of interaction with /127.0.0.1:\\k<clientPort>
                    Goodbye!""".replace("$serverPort", Integer.toString(port)),
                    server.stdoutAsText()
            );
        }
    }

    @Test
    public void singleEchoWorks() throws IOException, InterruptedException {
        try (TestableProcess server = startJavaProcess(EchoServer.class, port)) {
            try (TestableProcess client = startJavaProcess(EchoClient.class, "localhost", port)) {
                try (var clientStdin = client.stdin()) {
                    clientStdin.write("hello\n");
                }
                assertTrue(client.process().waitFor(3, TimeUnit.SECONDS));
                assertEquals(0, client.process().exitValue());
                client.printDebugInfo("client");
                assertTrue(client.stderrAsText().isBlank());
                assertMatches(
                        """
                        Contacting host localhost:$serverPort...
                        Connection established
                        Sent 6 bytes to localhost/127.0.0.1:$serverPort
                        (Reached end of input\\n?|Received 6 bytes from localhost/127.0.0.1:$serverPort\\n?){2,2}
                        hello
                        Received EOF from localhost/127.0.0.1:$serverPort
                        Goodbye!""".replace("$serverPort", Integer.toString(port)),
                        client.stdoutAsText()
                );
            }
            server.stdin().close();
            assertTrue(server.process().waitFor(3, TimeUnit.SECONDS));
            assertEquals(0, server.process().exitValue());
            server.printDebugInfo("server");
            assertTrue(server.stderrAsText().isBlank());
            assertMatches(
                    """
                    Bound to port $serverPort
                    Accepted connection from: /127.0.0.1:(?<clientPort>\\d+), on local port $serverPort
                    Echoed 6 bytes from /127.0.0.1:\\k<clientPort>
                    End of interaction with /127.0.0.1:\\k<clientPort>
                    Goodbye!""".replace("$serverPort", Integer.toString(port)),
                    server.stdoutAsText()
            );
        }
    }

    @Test
    public void clientsAreServedInParallel() throws IOException, InterruptedException {
        try (TestableProcess server = startJavaProcess(EchoServer.class, port)) {
            try (TestableProcess client2 = startJavaProcess(EchoClient.class, "localhost", port)) {
                client2.stdin().write("message 1\n");
                while (client2.stdoutAsLines().size() < 2) {
                    // waste time
                }
                try (TestableProcess client1 = startJavaProcess(EchoClient.class, "localhost", port)) {
                    client1.stdin().write("message 2\n");
                    client1.stdin().write("message 3\n");
                    client1.stdin().close();
                    assertTrue(client1.process().waitFor(3, TimeUnit.SECONDS));
                    assertEquals(0, client1.process().exitValue());
                    client1.printDebugInfo("client1");
                    assertTrue(client1.stderrAsText().isBlank());
                    assertRelativeOrderOfLines(
                            client1.stdoutAsText(),
                            "message 2",
                            "message 3",
                            "Goodbye!"
                    );
                }
                client2.stdin().write("message 4\n");
                client2.stdin().close();
                assertTrue(client2.process().waitFor(3, TimeUnit.SECONDS));
                assertEquals(0, client2.process().exitValue());
                client2.printDebugInfo("client2");
                assertTrue(client2.stderrAsText().isBlank());
                assertRelativeOrderOfLines(
                        client2.stdoutAsText(),
                        "message 1",
                        "message 4",
                        "Goodbye!"
                );
            }
            server.stdin().close();
            assertTrue(server.process().waitFor(3, TimeUnit.SECONDS));
            assertEquals(0, server.process().exitValue());
            server.printDebugInfo("server");
            assertTrue(server.stderrAsText().isBlank());
            var allServerLines = server.stdoutAsLines();
            var openingLines = allServerLines.stream().filter(it -> it.startsWith("Accepted connection from:")).toList();
            var closingLines = allServerLines.stream().filter(it -> it.startsWith("End of interaction with")).toList();
            assertEquals(2, openingLines.size());
            assertEquals(2, closingLines.size());
            for (var opening : openingLines) {
                for (var closing : closingLines) {
                    assertTrue(allServerLines.indexOf(opening) < allServerLines.indexOf(closing));
                }
            }
        }
    }
}
