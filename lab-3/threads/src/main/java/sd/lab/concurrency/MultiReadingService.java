package sd.lab.concurrency;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MultiReadingService {
    private final List<BufferedReader> inputs;
    private List<Thread> workers;

    public MultiReadingService(InputStream input1, InputStream... inputs) {
        this.inputs = Stream.concat(Stream.of(input1), Stream.of(inputs))
                .map(InputStreamReader::new)
                .map(BufferedReader::new)
                .collect(Collectors.toList());
    }

    private void handleReader(int index, BufferedReader reader) {
        try (reader) {
            var line = reader.readLine();
            while (line != null) {
                onLineRead(index, line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            onError(index, e);
        } finally {
            onInputClosed(index);
        }
    }

    public void start() {
        if (workers != null) throw new IllegalStateException("Service already started");
        workers = IntStream.range(0, inputs.size())
                .mapToObj(i -> new Thread(() -> handleReader(i, inputs.get(i))))
                .peek(Thread::start)
                .collect(Collectors.toList());
    }

    public void join() throws InterruptedException {
        if (workers == null) throw new IllegalStateException("Service not started yet");
        for (Thread worker : workers) {
            worker.join();
        }
    }

    public void join(long wait) throws InterruptedException {
        if (workers == null) throw new IllegalStateException("Service not started yet");
        for (Thread worker : workers) {
            worker.join(wait);
        }
    }

    public void onLineRead(int index, String line) {
        System.out.printf("Read from input %d: %s\n", index, line);
    }

    public void onInputClosed(int index) {
        System.out.printf("Input %d is over\n", index);
    }

    public void onError(int index, IOException error) {
        System.err.printf("Error while reading from (or closing) input %d: %s\n", index, error.getMessage());
    }
}
