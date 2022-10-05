package sd.lab.concurrency;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class PromisesExamples {

    private ExecutorService ex;

    @BeforeEach
    public void setUp() {
        ex = Executors.newSingleThreadExecutor(); // single thread!
    }

    @AfterEach
    public void tearDown() {
        ex.shutdownNow();
    }

    // cf. https://regex101.com/r/0bT8Uq/1
    private static final Pattern VERSION_FIELD = Pattern.compile(
            "\"tag_name\"\\s*:\\s*\"r(\\d+\\.\\d+\\.\\d+.*?)\""
    );


    // cf. https://github.com/junit-team/junit5/releases
    @Test
    public void testLastJUnitVersionRetrieval() throws ExecutionException, InterruptedException {
        HttpClient client = HttpClient.newBuilder().executor(ex).build();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("https://api.github.com/repos/junit-team/junit5/releases/latest"))
                .build();
        CompletableFuture<String> version = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(response -> response.body())
                .thenApplyAsync(body -> VERSION_FIELD.matcher(body))
                .thenApplyAsync(matcher -> matcher.find() ? matcher.group(1) : null);

        assertEquals("5.9.1", version.get());
    }

    @Test
    public void testWrongJUnitVersionRetrieval() throws InterruptedException {
        HttpClient client = HttpClient.newBuilder().executor(ex).build();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("https://api.github.com/repos/junit-team/junit5/releases/missing"))
                .build();
        CompletableFuture<String> version = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenComposeAsync(response -> {
                    if (response.statusCode() == 200) {
                        return CompletableFuture.completedFuture(response.body());
                    } else {
                        return CompletableFuture.failedFuture(new IllegalStateException("Status code is " + response.statusCode()));
                    }
                })
                .thenComposeAsync(body -> {
                    var matcher = VERSION_FIELD.matcher(body);
                    if (matcher.find()) {
                        return CompletableFuture.completedFuture(matcher.group(1));
                    } else {
                        return CompletableFuture.failedFuture(new IllegalStateException("Unexpected response body: " + body));
                    }
                });

        try {
            version.get();
            fail();
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof IllegalStateException);
            assertEquals("Status code is 404", e.getCause().getMessage());
        }
    }
}
