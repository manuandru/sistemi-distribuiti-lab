package sd.lab.concurrency;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sd.lab.concurrency.AssertUtils.suspendCurrentThread;

public class TestAsyncCounter1 {

    private ExecutorService ex;

    @BeforeEach
    public void setUp() {
        ex = Executors.newSingleThreadExecutor(); // single thread!
    }

    @AfterEach
    public void tearDown(){
        ex.shutdownNow();
    }

    @Test
    public void singleActivity() {
        AsyncCounter1 x = new AsyncCounter1(ex);

        assertEquals(0, x.getValue());
        x.countUpTo(10);

        suspendCurrentThread(1, TimeUnit.SECONDS);

        assertEquals(10, x.getValue());
    }

    @Test
    public void multipleActivities() {
        AsyncCounter1 x = new AsyncCounter1(0, ex);
        AsyncCounter1 y = new AsyncCounter1(10, ex);
        AsyncCounter1 z = new AsyncCounter1(20, ex);

        assertEquals(0, x.getValue());
        assertEquals(10, y.getValue());
        assertEquals(20, z.getValue());

        x.countUpTo(10);
        y.countUpTo(20);
        z.countUpTo(30);

        suspendCurrentThread(2, TimeUnit.SECONDS);

        assertEquals(10, x.getValue());
        assertEquals(20, y.getValue());
        assertEquals(30, z.getValue());
    }
}
