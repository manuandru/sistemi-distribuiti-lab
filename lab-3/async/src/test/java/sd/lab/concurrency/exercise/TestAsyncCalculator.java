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

        final BigInteger factorialOf500 = new BigInteger("1220136825991110068701238785423046926253574342803192842192413588385845373153881997605496447502203281863013616477148203584163378722078177200480785205159329285477907571939330603772960859086270429174547882424912726344305670173270769461062802310452644218878789465754777149863494367781037644274033827365397471386477878495438489595537537990423241061271326984327745715546309977202781014561081188373709531016356324432987029563896628911658974769572087926928871281780070265174507768410719624390394322536422605234945850129918571501248706961568141625359056693423813008856249246891564126775654481886506593847951775360894005745238940335798476363944905313062323749066445048824665075946735862074637925184200459369692981022263971952597190945217823331756934581508552332820762820023402626907898342451712006207714640979456116127629145951237229913340169552363850942885592018727433795173014586357570828355780158735432768888680120399882384702151467605445407663535984174430480128938313896881639487469658817504506926365338175055478128640000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");

        final CompletableFuture<BigInteger> longLasting = calculator.factorial(500);
        final CompletableFuture<BigInteger> shortLasting = calculator.factorial(1);

        assertEquals(BigInteger.ONE, shortLasting.get());
        assertTrue(shortLasting.isDone());
        assertFalse(longLasting.isDone());
        assertEquals(factorialOf500, longLasting.get());
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
