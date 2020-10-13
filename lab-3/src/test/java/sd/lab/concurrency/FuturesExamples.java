package sd.lab.concurrency;

import org.junit.Test;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

import static org.junit.Assert.*;
import static sd.lab.concurrency.AssertUtils.assertOneOf;
import static sd.lab.concurrency.AssertUtils.suspendCurrentThread;

public class FuturesExamples {

    @Test
    public void multiThreadedExecutor() throws InterruptedException, ExecutionException {
        final ExecutorService ex = Executors.newCachedThreadPool(); // multithreaded executor
        final List<Integer> events = new LinkedList<>();

        ex.execute(() -> events.add(1));
        ex.execute(() -> suspendCurrentThread(10, TimeUnit.SECONDS));

        final Future<Boolean> secondEvent = ex.submit(() -> events.add(2));
        secondEvent.get();

        ex.shutdownNow();

        assertEquals(List.of(1, 2), events);
    }

    @Test
    public void longComputationInBackground() throws ExecutionException, InterruptedException {
        final ExecutorService ex = Executors.newSingleThreadExecutor(); // single threaded executor
        final List<String> events = new LinkedList<>();

        final long computeFactorialOf = 100;
        final BigInteger expected = new BigInteger("93326215443944152681699238856266700490715968264381621468592963895217599993229915608941463976156518286253697920827223758251185210916864000000000000000000000000");

        final Future<BigInteger> result = ex.submit(() -> MathUtils.factorial(computeFactorialOf));

        events.add("0");
        events.add("" + result.isDone());
        System.out.println("This should appear soon");

        events.add("1");
        System.out.println("This should appear soon, too");

        events.add(result.get().toString());
        System.out.println("This should appear after a while");

        events.add("" + result.isDone());

        assertEquals(
                List.of("0", "false", "1", expected.toString(), "true"),
                events
        );
    }

}
