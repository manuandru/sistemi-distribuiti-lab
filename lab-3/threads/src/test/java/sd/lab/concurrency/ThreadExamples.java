package sd.lab.concurrency;

import org.javatuples.Pair;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sd.lab.concurrency.ResourcesUtils.openResource;

public class ThreadExamples {

    private static class TestableMultiReadingThread extends MultiReadingThread {
        private final List<Pair<Integer, String>> events;

        public TestableMultiReadingThread(List<Pair<Integer, String>> events, InputStream input1, InputStream... inputs) {
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
        var events = new LinkedList<Pair<Integer, String>>();
        var readingThread = new TestableMultiReadingThread(
                events,
                openResource("file1.txt"),
                openResource("file2.txt"),
                openResource("file3.txt")
        );
        readingThread.start();
        readingThread.join();
        assertEquals(
                List.of(
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
                ),
                events
        );
    }

    private static InputStream blockingInputStream() throws IOException {
        var output = new PipedOutputStream();
        var input = new PipedInputStream();
        input.connect(output);
        return input;
    }

    @Test
    public void multipleInputsBlocking() throws InterruptedException, IOException {
        var events = new LinkedList<Pair<Integer, String>>();
        var readingThread = new TestableMultiReadingThread(
                events,
                openResource("file1.txt"),
                openResource("file2.txt"),
                openResource("file3.txt"),
                blockingInputStream()
        );
        readingThread.start();
        readingThread.join(1000);
        assertEquals(
                List.of(
                        Pair.with(0, "a"),
                        Pair.with(1, "1"),
                        Pair.with(2, "alpha")
                ),
                events
        );
    }

}
