package sd.lab.concurrency;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;
import static sd.lab.concurrency.AssertUtils.suspendCurrentThread;

public class SplittingComputationsExamples {

    private ExecutorService ex;
    private List<Integer> events;
    private Counter x;
    private Counter y;

    @Before
    public void setUp() {
        ex = Executors.newSingleThreadExecutor(); // single thread!
        events = new LinkedList<>();
        x = new Counter(0);
        y = new Counter(0);
    }

    /**
     * Increases {@link #x} up to <code>max</code>, asynchronously
     * @param max
     */
    private void incCounterUpTo(int max) {
        events.add(x.getValue());
        x.inc();

        if (x.getValue() < max) {
            ex.execute(() -> incCounterUpTo(max)); // async recursion
        }
    }

    private void decCounterDownTo(int min) {
        events.add(y.getValue());
        y.dec();

        if (y.getValue() > min) {
            ex.execute(() -> decCounterDownTo(min)); // async recursion
        }
    }

    @After
    public void tearDown(){
        ex.shutdownNow();
    }

    /**
     * A simple example of how an asynchronous computation can be carried out on a single threaded executor
     */
    @Test
    public void loopOnExecutors() {

        ex.execute(() -> incCounterUpTo(5));

        suspendCurrentThread(1, TimeUnit.SECONDS);

        assertEquals(List.of(0, 1, 2, 3, 4), events);
    }

    /**
     * A simple example of how SEVERAL asynchronous computations can be carried out on a SINGLE threaded executor,
     * in a concurrent way
     */
    @Test
    public void twoConcurrentActivities() {
        ex.execute(() -> {
            ex.execute(() -> incCounterUpTo(5));
            ex.execute(() -> decCounterDownTo(-5));
        });

        suspendCurrentThread(3, TimeUnit.SECONDS);

        assertEquals(List.of(0, 0, 1, -1, 2, -2, 3, -3, 4, -4), events);
    }
}
