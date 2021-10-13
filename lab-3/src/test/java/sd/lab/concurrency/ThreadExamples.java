package sd.lab.concurrency;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ThreadExamples {

    static class ReadingThread extends Thread {
        private final List<BufferedReader> inputs;

        public ReadingThread(InputStream... inputs) {
            this.inputs = Stream.of(inputs)
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
                        System.out.printf("Read from input %d: %s\n", index, line);
                        if (line == null) {
                            reader.close();
                            System.out.printf("Input %d is over\n", index);
                            readerIterator.remove();
                        }
                    } catch (IOException e) {
                        System.err.printf("Error while reading from (or closing) input %d: %s\n", index, e.getMessage());
                        readerIterator.remove();
                    }
                }
            }
        }
    }

}
