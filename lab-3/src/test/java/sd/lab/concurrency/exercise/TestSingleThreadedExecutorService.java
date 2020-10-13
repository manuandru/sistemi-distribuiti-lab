package sd.lab.concurrency.exercise;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sd.lab.concurrency.Counter;
import sd.lab.concurrency.MathUtils;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.Assert.*;
import static sd.lab.concurrency.AssertUtils.assertOneOf;
import static sd.lab.concurrency.AssertUtils.suspendCurrentThread;

public class TestSingleThreadedExecutorService {
    private ExecutorService ex;

    @Before
    public void setUp() {
        ex = new SingleThreadedExecutorService();
    }

    @After
    public void tearDown() throws InterruptedException {
        ex.shutdownNow();
        assertTrue(ex.isShutdown());
        ex.awaitTermination(1, TimeUnit.SECONDS);
        assertTrue(ex.isTerminated());
    }

    /**
     * End-to-end demonstration of how executor services should be used
     */
    @Test
    public void usageOfAnExecutorService() throws InterruptedException {
        final List<Integer> events = new LinkedList<>();

        ex.execute(() -> events.add(1));  // asynchronously add 1 to events
        events.add(2);                    // *synchronously* add 2 to events
        ex.execute(() -> events.add(3));  // asynchronously add 3 to events

        ex.shutdown(); // prevents ex from accepting other tasks
        assertTrue(ex.isShutdown());

        try {
            ex.execute(() -> events.add(4)); // this should fail
            fail(); // let fail the test if the statement above does NOT throw an exception
        } catch (RejectedExecutionException ignored) {
            // intended effect of submitting a task to a shut-down executor:
            // a RejectedExecutionException should be thrown
            assertTrue(true);
        }

        // let the test's thread wait for the executor to be done with all the submitted tasks
        // waits no longer than Long.MAX_VALUE seconds
        ex.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        // takeaway: NEVER draw assertions while the executor is working

        // 3 events in total must have been occurred
        assertEquals(3, events.size());

        // there is no guarantee on how the first two events are ordered
        assertOneOf(Set.of(1, 2), events.get(0));
        assertOneOf(Set.of(2, 1), events.get(1));

        // event 3 must necessarily be the last one
        assertEquals(Integer.valueOf(3), events.get(2));
    }

    /**
     * Single-threaded executor services execute tasks in the same order they have ben scheduled.
     * THUS, if a task requires a lot of time, other tasks submitted after it will be delayed
     */
    @Test
    public void singleThreadedExecutor() {
        final List<Integer> events = new LinkedList<>();

        ex.execute(() -> events.add(1)); // a quick task
        ex.execute(() -> suspendCurrentThread(10, TimeUnit.SECONDS)); // a task requiring 10 seconds
        ex.execute(() -> events.add(2)); // another quick task

        // wait 1 second, then abruptly interrupts and terminates the executor service
        suspendCurrentThread(1, TimeUnit.SECONDS);
        ex.shutdownNow();
        assertTrue(ex.isShutdown());

        // the second task will be interrupted, the third one will never be executed

        assertEquals(List.of(1), events);
    }


    /**
     * Exceptions being thrown within tasks do NOT interrupt the executor service, nor they can stop it.
     */
    @Test
    public void exceptionsDoNotBreakExecutors() throws InterruptedException {
        final List<Integer> events = new LinkedList<>();
        final Supplier<Boolean> alwaysTrue = () -> true;

        ex.execute(() -> {
            if (alwaysTrue.get()) {
                throw new NullPointerException();
            }
            events.add(1);
        });

        ex.execute(() -> events.add(2));

        ex.shutdown();
        assertTrue(ex.isShutdown());
        ex.awaitTermination(1, TimeUnit.SECONDS);
        assertTrue(ex.isTerminated());

        assertEquals(List.of(2), events);
    }

    @Test
    public void longComputationInBackground() throws ExecutionException, InterruptedException {
        final List<String> events = new LinkedList<>();

        final long computeFactorialOf = 100;
        final BigInteger expected = new BigInteger("93326215443944152681699238856266700490715968264381621468592963895217599993229915608941463976156518286253697920827223758251185210916864000000000000000000000000");

        final Future<BigInteger> result = ex.submit(() -> MathUtils.factorial(computeFactorialOf));

        events.add("0");
        events.add("" + result.isDone());
        System.out.println("This should appear soon");

        events.add("1");
        System.out.println("This should appear soon, too");

        final BigInteger actualResult = result.get(); // the current thread is blocked until the result is ready
        events.add(actualResult.toString());
        System.out.println("This should appear after a while");

        events.add("" + result.isDone());

        assertEquals(
                List.of("0", "false", "1", expected.toString(), "true"),
                events
        );
    }

    @Test
    public void exceptionsCompleteFuturesExceptionally() throws InterruptedException {
        final List<Integer> events = new LinkedList<>();
        final Supplier<Boolean> alwaysTrue = () -> true;

        Future<Integer> task = ex.submit(() -> {
            if (alwaysTrue.get()) {
                throw new NullPointerException();
            }
            events.add(1);
            return 1;
        });

        ex.execute(() -> events.add(2));

        ex.shutdown();
        ex.awaitTermination(1, TimeUnit.SECONDS);

        assertEquals(List.of(2), events);

        // notice that task should be failed at this point, because of the NullPointerException...
        try {
            // ... thus, any attempt to retrieve the result of task ...
            task.get();
        } catch (ExecutionException e) { // ... will produce an ExecutionException
            // notice that the cause of e will be the same NullPointerException which made task fail
            assertTrue(e.getCause() instanceof NullPointerException);
        }
    }

    /**
     * Starts an asynchronous activity aimed at increasing a counter from 0 to <code>max</code>
     *
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
        final CompletableFuture<Integer> promisedResult = incCounterUpTo(5)
                .thenApply(r -> r * 2);

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
