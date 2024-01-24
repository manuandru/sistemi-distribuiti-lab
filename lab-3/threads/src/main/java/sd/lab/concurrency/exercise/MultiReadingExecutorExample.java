package sd.lab.concurrency.exercise;

import static sd.lab.concurrency.ResourcesUtils.openResource;

public class MultiReadingExecutorExample {
    public static void main(String... args) throws InterruptedException {
        var readingThread = new MultiReadingExecutor(
                openResource("file1.txt"),
//                System.in,
                openResource("file2.txt"),
                openResource("file3.txt")
        );
        readingThread.start();
        readingThread.join();
    }
}
