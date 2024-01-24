package sd.lab.concurrency;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * Asynchronous counter #2: each counting step is scheduled as a different task on an {@link ExecutorService},
 * exactly as in {@link AsyncCounter1}.
 * Clients may *start* counting via the {@link #countUpTo(int)} method.
 * However, here {@link #countUpTo(int)} returns a {@link CompletableFuture}, which clients may exploit to wait for the
 * counting to be over.
 */
public final class AsyncCounter2 {

    private final ExecutorService executor;
    private volatile int value;

    public AsyncCounter2(int initialValue, ExecutorService executor) {
        this.executor = executor;
        this.value = initialValue;
    }

    public AsyncCounter2(ExecutorService executor) {
        this(0, executor);
    }

    /**
     * Starts an asynchronous counting which will eventually lead {@link #value} to be equal to <code>max</code>
     * @param max it the value to be reached via counting
     * @return a {@link CompletableFuture} which will be completed as soon as {@link #value} becomes
     * equals to <code>max</code>
     */
    public CompletableFuture<Integer> countUpTo(int max) {
        // creates a promise which will contain the result of the asynchronous computation
        final CompletableFuture<Integer> resultPromise = new CompletableFuture<>();

        // schedules the asynchronous computation on the executor
        // notice that the promise is passed as argument to countUpToImpl, since it must eventually be completed
        executor.execute(() -> countUpToImpl(max, resultPromise));

        // the promise is returned immediately to the client
        return resultPromise;
    }

    private synchronized void countUpToImpl(int max, CompletableFuture<Integer> result) {
        value++;
        if (value < max) {
            executor.execute(() -> countUpToImpl(max, result)); // recursion!
        } else {
            result.complete(value);
        }
    }

    public synchronized int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AsyncCounter2 that = (AsyncCounter2) o;
        return value == that.value &&
                Objects.equals(executor, that.executor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(executor, value);
    }

    @Override
    public String toString() {
        return "AsyncCounter2{" +
                "value=" + value +
                ", executor=" + executor +
                '}';
    }
}
