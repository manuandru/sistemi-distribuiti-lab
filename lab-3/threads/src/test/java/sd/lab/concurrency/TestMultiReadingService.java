package sd.lab.concurrency;

import org.javatuples.Pair;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Collection;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sd.lab.concurrency.MultiReadingTestUtils.EXPECTED_EVENTS_SET;
import static sd.lab.concurrency.MultiReadingTestUtils.TIMEOUT;
import static sd.lab.concurrency.ResourcesUtils.openResource;

public class TestMultiReadingService {

    private static class TestableMultiReadingService extends MultiReadingService {
        private final Collection<Pair<Integer, String>> events;

        public TestableMultiReadingService(Collection<Pair<Integer, String>> events, InputStream input1, InputStream... inputs) {
            super(input1, inputs);
            this.events = events;
        }

        @Override
        public void onLineRead(int index, String line) {
            events.add(Pair.with(index, line));
        }

        @Override
        public void onError(int index, IOException error) {
            events.add(Pair.with(index, error.getMessage()));
        }

        @Override
        public void onInputClosed(int index) {
            events.add(Pair.with(index, null));
        }
    }

    @Test
    public void multipleInputsNonBlocking() throws InterruptedException {
        var events = new HashSet<Pair<Integer, String>>();
        var readingThread = new TestableMultiReadingService(
                events,
                openResource("file1.txt"),
                openResource("file2.txt"),
                openResource("file3.txt")
        );
        readingThread.start();
        readingThread.join();
        assertEquals(EXPECTED_EVENTS_SET, events);
    }

    private static InputStream blockingInputStream() throws IOException {
        var output = new PipedOutputStream();
        var input = new PipedInputStream();
        input.connect(output);
        return input;
    }

    @Test
    public void multipleInputsBlocking() throws InterruptedException, IOException {
        var events = new HashSet<Pair<Integer, String>>();
        var readingThread = new TestableMultiReadingService(
                events,
                openResource("file1.txt"),
                openResource("file2.txt"),
                openResource("file3.txt"),
                blockingInputStream()
        );
        readingThread.start();
        readingThread.join(TIMEOUT);
        assertEquals(EXPECTED_EVENTS_SET, events);
    }

}
