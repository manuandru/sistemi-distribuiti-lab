package sd.lab.concurrency;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AssertUtils {
    public static List<Integer> range(int minInclusive, int length) {
        return IntStream.range(minInclusive, minInclusive + length).boxed().collect(Collectors.toList());
    }

    public static void suspendCurrentThread(long duration, TimeUnit timeUnit) {
        try {
            Thread.sleep(timeUnit.toMillis(duration));
        } catch (InterruptedException ignored) {
            // does nothing
        }
    }

    public static <T> void assertOneOf(String message, Collection<? extends T> expected, T actual) {
        assertTrue(expected.contains(actual), message);
    }

    public static <T> void assertOneOf(Collection<? extends T> expected, T actual) {
        assertTrue(
                expected.contains(actual),
                expected.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(", ", actual + " is not in { ", " }"))
        );
    }

}
