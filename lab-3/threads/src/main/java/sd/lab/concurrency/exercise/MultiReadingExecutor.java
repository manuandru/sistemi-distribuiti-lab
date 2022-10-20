package sd.lab.concurrency.exercise;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MultiReadingExecutor {
    private final List<BufferedReader> inputs;
    private ExecutorService executorService;
    private AtomicInteger unfinishedReaders = new AtomicInteger();

    public MultiReadingExecutor(InputStream input1, InputStream... inputs) {
        this.inputs = Stream.concat(Stream.of(input1), Stream.of(inputs))
                .map(InputStreamReader::new)
                .map(BufferedReader::new)
                .collect(Collectors.toList());
    }

    private void handleReader(int index, BufferedReader reader) {
        try {
            var line = reader.readLine();
            if (line != null) {
                onLineRead(index, line);
                executorService.execute(() -> handleReader(index, reader));
            } else {
                reader.close();
                onInputClosed(index);
                if (unfinishedReaders.decrementAndGet() == 0) {
                    executorService.shutdown();
                }
            }
        } catch (IOException e) {
            onError(index, e);
        }
    }

    public void start() {
        if (executorService != null) throw new IllegalStateException("Executor already started");
        executorService = Executors.newCachedThreadPool();
        unfinishedReaders.set(inputs.size());
        IntStream.range(0, inputs.size())
                .forEach(i -> executorService.execute(() -> handleReader(i, inputs.get(i))));
    }

    public void join() throws InterruptedException {
        // I think that after ~2.5 * 10^16 years the world is end :D
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }

    public void join(long wait) throws InterruptedException {
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
