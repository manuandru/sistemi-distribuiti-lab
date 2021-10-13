package sd.lab.concurrency;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestAsyncCounter2 {

    private ExecutorService ex;

    @BeforeEach
    public void setUp() {
        ex = Executors.newSingleThreadExecutor(); // single thread!
    }

    @BeforeEach
    public void tearDown(){
        ex.shutdownNow();
    }

    @Test
    public void singleActivity() throws ExecutionException, InterruptedException {
        AsyncCounter2 x = new AsyncCounter2(ex);

        assertEquals(0, x.getValue());
        final CompletableFuture<Integer> result =  x.countUpTo(10_000);

        assertEquals(Integer.valueOf(10_000), result.get()); // blocking!
        assertEquals(10_000, x.getValue());
    }

    @Test
    public void multipleActivities() throws ExecutionException, InterruptedException {
        AsyncCounter2 x = new AsyncCounter2(0, ex);
        AsyncCounter2 y = new AsyncCounter2(10, ex);
        AsyncCounter2 z = new AsyncCounter2(20, ex);

        assertEquals(0, x.getValue());
        assertEquals(10, y.getValue());
        assertEquals(20, z.getValue());

        final CompletableFuture<Integer> resX =  x.countUpTo(10);
        final CompletableFuture<Integer> resY =  y.countUpTo(20);
        final CompletableFuture<Integer> resZ =  z.countUpTo(30);

        CompletableFuture.allOf(resX, resY, resZ).get(); // blocking!

        assertEquals(10, x.getValue());
        assertEquals(20, y.getValue());
        assertEquals(30, z.getValue());
    }
}
