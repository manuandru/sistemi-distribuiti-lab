package sd.lab.concurrency;

import org.javatuples.Pair;

import java.time.Duration;
import java.util.List;
import java.util.Set;

public class MultiReadingTestUtils {
    public static final long TIMEOUT = Duration.ofSeconds(2).toMillis();

    public static final List<Pair<Integer, String>> EXPECTED_EVENTS_LIST = List.of(
            Pair.with(0, "a"),
            Pair.with(1, "1"),
            Pair.with(2, "alpha"),
            Pair.with(0, "b"),
            Pair.with(1, "2"),
            Pair.with(2, "beta"),
            Pair.with(0, "c"),
            Pair.with(1, "3"),
            Pair.with(2, "gamma"),
            Pair.with(0, null),
            Pair.with(1, null),
            Pair.with(2, null)
    );

    public static final Set<Pair<Integer, String>> EXPECTED_EVENTS_SET = Set.copyOf(EXPECTED_EVENTS_LIST);
}
