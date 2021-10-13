package sd.lab.concurrency;


import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static sd.lab.concurrency.AssertUtils.suspendCurrentThread;

public class FuturesExamples {

    /**
     * The method {@link ExecutorService#submit(Callable)} can be used by clients willing to schedule a task on an
     * executor, when the task is aimed at producing a result which should eventually be consumed by the client.
     *
     * In this example, the computation of the factorial of 100 (which is a pretty high number) is performed
     * in background using {@link ExecutorService#submit(Callable)}.
     * A future is returned immediately, despite the computation may take a while.
     * Through a the {@link Future#get()} method, clients can await for an asynchronous result to be available.
     *
     * TAKEAWAY: await for a result == blocking the current thread until the result is ready
     *
     * REMARK: {@link BigInteger}s are used to avoid overflow issues while computing factorials
     */
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

        final BigInteger actualResult = result.get(); // the current thread is blocked until the result is ready
        events.add(actualResult.toString());
        System.out.println("This should appear after a while");

        events.add("" + result.isDone());

        assertEquals(
                List.of("0", "false", "1", expected.toString(), "true"),
                events
        );
    }

    /**
     * On multi-threaded executors, long-lasting tasks do NOT delay other tasks scheduled after them
     *
     * (This way of withing such sorts of tests is more correct, as there is no time-dependent behaviour)
     */
    @Test
    public void multiThreadedExecutor() throws InterruptedException, ExecutionException {
        final ExecutorService ex = Executors.newCachedThreadPool(); // multithreaded executor
        final List<Integer> events = new LinkedList<>();

        ex.execute(() -> events.add(1));
        ex.execute(() -> suspendCurrentThread(10, TimeUnit.SECONDS));

        final Future<Boolean> thirdTask = ex.submit(() -> events.add(2));
        thirdTask.get();

        ex.shutdownNow();

        assertEquals(List.of(1, 2), events);
    }

    /**
     * Exceptions being thrown within tasks do NOT interrupt the executor service, nor they can stop it.
     * Furthermore, if the task has been scheduled wia the {@link ExecutorService#submit(Callable)} method,
     * and someone tries invokes {@link Future#get()} on the future returned by that method, then an
     * {@link ExecutionException} is thrown. The exception occurred within the task will be stored as the cause (cf.
     * {@link Exception#getCause()} of the aforementioned {@link ExecutionException}.
     */
    @Test
    public void exceptionsCompleteFuturesExceptionally() throws InterruptedException {
        final ExecutorService ex = Executors.newSingleThreadExecutor();
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
}
