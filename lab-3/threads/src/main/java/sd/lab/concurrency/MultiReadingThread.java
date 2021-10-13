package sd.lab.concurrency;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MultiReadingThread extends Thread {
    private final List<BufferedReader> inputs;

    public MultiReadingThread(InputStream input1, InputStream... inputs) {
        this.inputs = Stream.concat(Stream.of(input1), Stream.of(inputs))
                .map(InputStreamReader::new)
                .map(BufferedReader::new)
                .collect(Collectors.toList());
    }

    @Override
    public void run() {
        while (!inputs.isEmpty()) {
            var index = 0;
            var readerIterator = inputs.iterator();
            for (; readerIterator.hasNext(); index++) {
                var reader = readerIterator.next();
                try {
                    var line = reader.readLine();
                    if (line == null) {
                        reader.close();
                        onInputClosed(index);
                        readerIterator.remove();
                    } else {
                        onLineRead(index, line);
                    }
                } catch (IOException e) {
                    onError(index, e);
                    readerIterator.remove();
                }
            }
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
