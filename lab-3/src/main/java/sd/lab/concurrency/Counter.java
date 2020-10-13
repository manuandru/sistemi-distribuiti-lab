package sd.lab.concurrency;

import java.util.Objects;

public final class Counter {
    private int value;

    public Counter(int value) {
        this.value = value;
    }

    public Counter() {
        this(0);
    }

    public Counter inc() {
        this.value++;
        return this;
    }

    public Counter inc(int i) {
        this.value += i;
        return this;
    }

    public Counter dec(int i) {
        this.value -= i;
        return this;
    }

    public Counter dec() {
        this.value--;
        return this;
    }

    @Override
    public String toString() {
        return "Counter{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Counter counter = (Counter) o;
        return value == counter.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    public int getValue() {
        return value;
    }

}

