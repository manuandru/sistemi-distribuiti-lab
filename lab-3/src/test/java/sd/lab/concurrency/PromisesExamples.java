package sd.lab.concurrency;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class PromisesExamples {

    private ExecutorService ex;

    @BeforeEach
    public void setUp() {
        ex = Executors.newSingleThreadExecutor(); // single thread!
    }

    @AfterEach
    public void tearDown(){
        ex.shutdownNow();
    }

    /**
     * Starts an asynchronous activity aimed at increasing a counter from 0 to <code>max</code>
     * @param max
     * @return a {@link CompletableFuture} letting clients know when the activity is over
     */
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

    /**
     * Example showing how to await for a {@link CompletableFuture}'s result
     */
    @Test
    public void completableFutureExample1() throws ExecutionException, InterruptedException {
        final CompletableFuture<Integer> promisedResult = incCounterUpTo(5);

        Integer actualResult = promisedResult.get(); // this is where the result is awaited
        assertEquals(Integer.valueOf(5), actualResult);
    }

    /**
     * Method {@link CompletableFuture#thenApply(Function)} is to {@link CompletableFuture} what
     * {@link java.util.stream.Stream#map(Function)} is to {@link java.util.stream.Stream}: it returns a novel
     * {@link CompletableFuture} attained by applying the provided {@link Function} to the source
     * {@link CompletableFuture}'s result, whenever it becomes available
     */
    @Test
    public void completableFutureExample2() throws ExecutionException, InterruptedException {
        final CompletableFuture<Integer> promisedResult = incCounterUpTo(5).thenApply(r -> r * 2);

        assertEquals(Integer.valueOf(10), promisedResult.get());
    }

    /**
     * Method {@link CompletableFuture#whenComplete(BiConsumer)} lets clients register a callback aimed at intercepting
     * the completion of a {@link CompletableFuture}, without creating a new {@link CompletableFuture}
     */
    @Test
    public void completableFutureExample3() throws ExecutionException, InterruptedException {
        final List<Integer> events = new LinkedList<>();

        final CompletableFuture<Integer> promisedResult = incCounterUpTo(5)
                .whenComplete((res, err) -> events.add(res))
                .thenApply(r -> r * 2);

        assertEquals(Integer.valueOf(10), promisedResult.get());
        assertEquals(List.of(5), events);
    }

    /**
     * The static method {@link CompletableFuture#anyOf(CompletableFuture[])} accepts a number of {@link CompletableFuture}s
     * and returns a novel {@link CompletableFuture} which is completed as soon as one of the aforementioned
     * {@link CompletableFuture}s complete
     */
    @Test
    public void joinPromisesOR() throws ExecutionException, InterruptedException {

        final CompletableFuture<?> promisedResult = CompletableFuture.anyOf(
                incCounterUpTo(1_000_000),
                incCounterUpTo(1_000),
                incCounterUpTo(10)
        );
        assertEquals(Integer.valueOf(10), promisedResult.get());

    }

    /**
     * The static method {@link CompletableFuture#allOf(CompletableFuture[])} accepts a number of {@link CompletableFuture}s
     * and returns a novel {@link CompletableFuture} which is completed as soon as ALL the aforementioned
     * {@link CompletableFuture}s complete
     */
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
