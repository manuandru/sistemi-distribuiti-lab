package sd.lab.concurrency;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.*;

public class PromisesExamples {

    private ExecutorService ex;

    @Before
    public void setUp() {
        ex = Executors.newSingleThreadExecutor(); // single thread!
    }

    @After
    public void tearDown(){
        ex.shutdownNow();
    }

    public CompletableFuture<Integer> incCounterUpTo(int max) {
        final CompletableFuture<Integer> result = new CompletableFuture<>();
        ex.execute(() -> incCounterUpToImpl(new Counter(0), max, result));
        return result;
    }

    private void incCounterUpToImpl(Counter x, int max, CompletableFuture<Integer> result) {
        x.inc();

        if (x.getValue() < max) {
            ex.execute(() -> incCounterUpToImpl(x, max, result)); // async recursion
        } else {
            result.complete(x.getValue());
        }
    }

    @Test
    public void completableFutureExample1() throws ExecutionException, InterruptedException {

        final CompletableFuture<Integer> promisedResult = incCounterUpTo(5);

        assertEquals(Integer.valueOf(5), promisedResult.get());
    }

    @Test
    public void completableFutureExample2() throws ExecutionException, InterruptedException {

        final CompletableFuture<Integer> promisedResult = incCounterUpTo(5).thenApply(r -> r * 2);

        assertEquals(Integer.valueOf(10), promisedResult.get());
    }

    @Test
    public void completableFutureExample3() throws ExecutionException, InterruptedException {

        final List<Integer> events = new LinkedList<>();

        final CompletableFuture<Integer> promisedResult = incCounterUpTo(5)
                .whenComplete((res, err) -> events.add(res))
                .thenApply(r -> r * 2);

        assertEquals(Integer.valueOf(10), promisedResult.get());
        assertEquals(List.of(5), events);
    }

    @Test
    public void joinPromisesOR() throws ExecutionException, InterruptedException {

        final CompletableFuture<?> promisedResult = CompletableFuture.anyOf(
                incCounterUpTo(1_000_000),
                incCounterUpTo(1_000),
                incCounterUpTo(10)
        );
        assertEquals(Integer.valueOf(10), promisedResult.get());

    }

    @Test
    public void joinPromisesAND() throws ExecutionException, InterruptedException {

        final CompletableFuture<Integer> ten, thousand, million;

        final CompletableFuture<Void> promisedResult = CompletableFuture.allOf(
                million = incCounterUpTo(1_000_000),
                thousand = incCounterUpTo(1_000),
                ten = incCounterUpTo(10)
        );

        assertNull(promisedResult.get());
        assertTrue(million.isDone());
        assertTrue(thousand.isDone());
        assertTrue(ten.isDone());

    }

}
