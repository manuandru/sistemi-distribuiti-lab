package sd.lab.concurrency.exercise;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MultiReadingExecutor {
    private final List<BufferedReader> inputs;
    private ExecutorService executorService;

    public MultiReadingExecutor(InputStream input1, InputStream... inputs) {
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
        if (executorService != null) throw new IllegalStateException("Executor already started");
        executorService = Executors.newCachedThreadPool();
        IntStream.range(0, inputs.size()).forEach(i -> executorService.submit(() -> handleReader(i, inputs.get(i))));
        executorService.shutdown();
    }

    public void join() throws InterruptedException {
        join(Long.MAX_VALUE);
    }

    public void join(long wait) throws InterruptedException {
        if (executorService == null) throw new IllegalStateException("Executor not started yet");
        executorService.awaitTermination(wait, TimeUnit.MILLISECONDS);
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
