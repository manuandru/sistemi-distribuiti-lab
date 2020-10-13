package sd.lab.concurrency;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

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

    public CompletableFuture<Integer> countUpTo(int max) {
        final CompletableFuture<Integer> resultPromise = new CompletableFuture<>();
        executor.execute(() -> countUpToImpl(max, resultPromise));
        return resultPromise;
    }

    private void countUpToImpl(int max, CompletableFuture<Integer> result) {
        value++;
        if (value < max) {
            executor.execute(() -> countUpToImpl(max, result)); // recursion!
        } else {
            result.complete(value);
        }
    }

    public int getValue() {
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
