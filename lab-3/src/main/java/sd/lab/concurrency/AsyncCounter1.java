package sd.lab.concurrency;

import java.util.Objects;
import java.util.concurrent.ExecutorService;

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

    public void countUpTo(int max) {
        executor.execute(() -> countUpToImpl(max));
    }

    private void countUpToImpl(int max) {
        value++;
        if (value < max) {
            countUpTo(max);
        }
    }

    public int getValue() {
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
