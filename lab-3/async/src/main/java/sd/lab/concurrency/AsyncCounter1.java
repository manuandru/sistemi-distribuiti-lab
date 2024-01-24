package sd.lab.concurrency;

import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * Asynchronous counter #1: each counting step is scheduled as a different task on an {@link ExecutorService}.
 * In this way, the {@link ExecutorService} may carry on other tasks as well, while counting.
 * Clients may *start* counting via the {@link #countUpTo(int)} method.
 *
 * PROBLEM: how can clients know when the counting is over?
 */
public final class AsyncCounter1 {

    private final ExecutorService executor;
    private volatile int value;

    public AsyncCounter1(int initialValue, ExecutorService executor) {
        this.executor = executor;
        this.value = initialValue;
    }

    public AsyncCounter1(ExecutorService executor) {
        this(0, executor);
    }

    /**
     * Starts an asynchronous counting which will eventually lead {@link #value} to be equal to <code>max</code>
     * @param max it the value to be reached via counting
     */
    public void countUpTo(int max) {
        // a new call to countUpToImpl(int) is scheduled on the executor
        executor.execute(() -> countUpToImpl(max));
    }

    /**
     * Actually increases {@link #value}. Then, if <code>value</code> if still lower than <code>max</code>, this method
     * calls {@link #countUpTo(int)}, which recursively schedules {@link #countUpToImpl(int)} again. The recursion
     * terminates when {@link #value} is equal to <code>max</code>
     * @param max it the value to be reached via counting
     */
    private synchronized void countUpToImpl(int max) {
        value++;
        if (value < max) {
            countUpTo(max); // recursion
        }
    }

    /**
     * Let clients read {@link #value}. Notice that there is no way for clients to know whether the counter is still
     * counting or not. Thus, there is no way for clients to know whether the returned value is definitive or not.
     * @return the current value of {@link #value}
     */
    public synchronized int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AsyncCounter1 that = (AsyncCounter1) o;
        return value == that.value &&
                Objects.equals(executor, that.executor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(executor, value);
    }

    @Override
    public String toString() {
        return "AsyncCounter1{" +
                "value=" + value +
                ", executor=" + executor +
                '}';
    }
}
