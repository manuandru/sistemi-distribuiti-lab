package sd.lab.concurrency.exercise;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
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

    public void start() {
        throw new Error("TODO: implement");
    }

    public void join() throws InterruptedException {
        throw new Error("TODO: implement");
    }

    public void join(long wait) throws InterruptedException {
        throw new Error("TODO: implement");
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
