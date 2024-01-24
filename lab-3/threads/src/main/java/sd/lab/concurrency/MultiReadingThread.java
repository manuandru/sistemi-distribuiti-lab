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
        // creates a list of BufferedReaders out of the provided InputStreams
        this.inputs = Stream.concat(Stream.of(input1), Stream.of(inputs))
                .map(InputStreamReader::new)
                .map(BufferedReader::new)
                .collect(Collectors.toList());
    }

    @Override
    public void run() {
        // while the list of inputs is not empty (assuming that input streams are removed once over)
        while (!inputs.isEmpty()) {
            // iterate over the input list via iterators (so that items can be removed during iteration)
            var readerIterator = inputs.iterator();
            var index = 0;
            for (; readerIterator.hasNext(); index++) {
                // reader is the current input in the iteration, whose position is index
                var reader = readerIterator.next();
                try {
                    // try to read a single line from the current reader (returns null if no more line is available)
                    var line = reader.readLine();
                    // if the current input is over...
                    if (line == null) {
                        // closes the input
                        reader.close();
                        // calls the onInputClosed callback
                        onInputClosed(index);
                        // removes the current input from the inputs list
                        readerIterator.remove();
                    } else { // otherwise, if a line has been read...
                        // calls the onLineRead callback
                        onLineRead(index, line);
                    }
                } catch (IOException e) { // if an error occurs while reading or closing...
                    // calls the onError callback
                    onError(index, e);
                    // removes the current input from the inputs list
                    readerIterator.remove();
                }
            }
        }
    }

    /**
     * Callback to be invoked whenever a new <code>line</code> is read on input with position <code>index</code>.
     * (May be overridden)
     *
     * @param index the position of the input a new line has been read from (w.r.t. the input streams provided upon construction)
     * @param line the line of text read from the <code>index</code>-th input
     */
    public void onLineRead(int index, String line) {
        System.out.printf("Read from input %d: %s\n", index, line);
    }

    /**
     * Callback to be invoked whenever the input with position <code>index</code> is closed.
     * (May be overridden)
     *
     * @param index the position of the input a new line has been read from (w.r.t. the input streams provided upon construction)
     */
    public void onInputClosed(int index) {
        System.out.printf("Input %d is over\n", index);
    }

    /**
     * Callback to be invoked whenever an {@link IOException} occurs while operating with the input with position <code>index</code>.
     * (May be overridden)
     *
     * @param index the position of the input a new line has been read from (w.r.t. the input streams provided upon construction)
     * @param error the {@link IOException} which has occurred
     */
    public void onError(int index, IOException error) {
        System.err.printf("Error while reading from (or closing) input %d: %s\n", index, error.getMessage());
    }
}
