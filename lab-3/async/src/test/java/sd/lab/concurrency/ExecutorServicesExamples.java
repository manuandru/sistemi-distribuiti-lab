package sd.lab.concurrency;

import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static sd.lab.concurrency.AssertUtils.assertOneOf;
import static sd.lab.concurrency.AssertUtils.suspendCurrentThread;

public class ExecutorServicesExamples {

    /**
     * End-to-end demonstration of how executor services should be used
     */
    @Test
    public void usageOfAnExecutorService() throws InterruptedException {
        // create a single-threaded executor named ex
        final ExecutorService ex = Executors.newSingleThreadExecutor();

        // list of observable events, to draw assertions
        final List<Integer> events = new LinkedList<>();

        ex.execute(() -> events.add(1));  // asynchronously add 1 to events
        events.add(2);                    // *synchronously* add 2 to events
        ex.execute(() -> events.add(3));  // asynchronously add 3 to events

        ex.shutdown(); // prevents ex from accepting other tasks

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
        final ExecutorService ex = Executors.newSingleThreadExecutor();
        final List<Integer> events = new LinkedList<>();

        ex.execute(() -> events.add(1)); // a quick task
        ex.execute(() -> suspendCurrentThread(10, TimeUnit.SECONDS)); // a task requiring 10 seconds
        ex.execute(() -> events.add(2)); // another quick task

        // wait 1 second, then abruptly interrupts and terminates the executor service
        suspendCurrentThread(1, TimeUnit.SECONDS);
        ex.shutdownNow();

        // the second task will be interrupted, the third one will never be executed

        assertEquals(List.of(1), events);
    }

    /**
     * On multi-threaded executors, long-lasting tasks do NOT delay other tasks scheduled after them
     *
     * (NOTICE: this way of writing tests is sub-optimal: the success of the test is time-dependent)
     */
    @Test
    public void multiThreadedExecutor() {
        final ExecutorService ex = Executors.newCachedThreadPool(); // multithreaded executor
        final List<Integer> events = new LinkedList<>();

        ex.execute(() -> events.add(1));
        ex.execute(() -> suspendCurrentThread(10, TimeUnit.SECONDS));
        ex.execute(() -> events.add(2));

        suspendCurrentThread(1, TimeUnit.SECONDS);
        ex.shutdownNow();

        assertEquals(List.of(1, 2), events);
    }

    /**
     * Exceptions being thrown within tasks do NOT interrupt the executor service, nor they can stop it.
     */
    @Test
    public void exceptionsDoNotBreakExecutors() throws InterruptedException {
        final ExecutorService ex = Executors.newSingleThreadExecutor(); // multithreaded executor
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
        ex.awaitTermination(1, TimeUnit.SECONDS);

        assertEquals(List.of(2), events);
    }

}
