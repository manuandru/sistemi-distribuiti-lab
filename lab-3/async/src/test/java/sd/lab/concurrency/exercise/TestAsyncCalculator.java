package sd.lab.concurrency.exercise;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sd.lab.concurrency.MathUtils;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class TestAsyncCalculator {
    private ExecutorService ex;

    @BeforeEach
    public void setUp() {
        ex = Executors.newSingleThreadExecutor(); // single thread!
    }

    @AfterEach
    public void tearDown() {
        ex.shutdownNow();
    }

    @Test
    public void testTrivialResults() throws ExecutionException, InterruptedException {
        final AsyncFactorialCalculator calculator = AsyncFactorialCalculator.newInstance(ex);

        final List<CompletableFuture<BigInteger>> results = IntStream.rangeClosed(0, 10)
                .mapToObj(calculator::factorial)
                .collect(Collectors.toList());

        for (int i = 0; i < results.size(); i++) {
            assertEquals(MathUtils.factorial(i), results.get(i).get());
        }
    }

    @Test
    public void testComputationIsProperlySplit() throws ExecutionException, InterruptedException {
        final AsyncFactorialCalculator calculator = AsyncFactorialCalculator.newInstance(ex);

        final BigInteger factorialOf100 = new BigInteger("93326215443944152681699238856266700490715968264381621468592963895217599993229915608941463976156518286253697920827223758251185210916864000000000000000000000000");

        final CompletableFuture<BigInteger> longLasting = calculator.factorial(100);
        final CompletableFuture<BigInteger> shortLasting = calculator.factorial(1);

        assertEquals(BigInteger.ONE, shortLasting.get());
        assertTrue(shortLasting.isDone());
        assertFalse(longLasting.isDone());
        assertEquals(factorialOf100, longLasting.get());
        assertTrue(longLasting.isDone());
    }

    @Test
    public void testFailure() throws InterruptedException {
        final AsyncFactorialCalculator calculator = AsyncFactorialCalculator.newInstance(ex);

        final CompletableFuture<BigInteger> failedResult = calculator.factorial(-1);

        try {
            failedResult.get();
            fail();
        } catch (ExecutionException exception) {
            assertTrue(exception.getCause() instanceof IllegalArgumentException);
            assertEquals("Cannot compute factorial for negative numbers", exception.getCause().getMessage());
        }
    }
}
