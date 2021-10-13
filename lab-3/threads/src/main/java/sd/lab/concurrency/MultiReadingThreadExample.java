package sd.lab.concurrency;

import static sd.lab.concurrency.ResourcesUtils.openResource;

public class MultiReadingThreadExample {
    public static void main(String... args) throws InterruptedException {
        var readingThread = new MultiReadingThread(
                openResource("file1.txt"),
//                System.in,
                openResource("file2.txt"),
                openResource("file3.txt")
        );
        readingThread.start();
        readingThread.join();
    }
}
