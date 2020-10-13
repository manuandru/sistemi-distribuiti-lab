package sd.lab.concurrency;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static org.junit.Assert.*;
import static sd.lab.concurrency.AssertUtils.assertOneOf;
import static sd.lab.concurrency.AssertUtils.suspendCurrentThread;

public class ExecutorServicesExamples {
    @Test
    public void usageOfAnExecutorService() throws InterruptedException {
        final ExecutorService ex = Executors.newSingleThreadExecutor();
        final List<Integer> events = new LinkedList<>();

        ex.execute(() -> events.add(1));
        events.add(2);
        ex.execute(() -> events.add(3));

        ex.shutdown();

        try {
            ex.execute(() -> events.add(4));
            fail();
        } catch (RejectedExecutionException ignored) {
            assertTrue(true);
        }

        ex.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);

        assertEquals(3, events.size());

        assertOneOf(Set.of(1, 2), events.get(0));
        assertOneOf(Set.of(2, 1), events.get(1));

        assertEquals(Integer.valueOf(3), events.get(2));
    }

    @Test
    public void singleThreadedExecutor() {
        final ExecutorService ex = Executors.newSingleThreadExecutor();
        final List<Integer> events = new LinkedList<>();

        ex.execute(() -> events.add(1));
        ex.execute(() -> suspendCurrentThread(10, TimeUnit.SECONDS));
        ex.execute(() -> events.add(2));

        suspendCurrentThread(1, TimeUnit.SECONDS);
        ex.shutdownNow();

        assertEquals(List.of(1), events);
    }

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
