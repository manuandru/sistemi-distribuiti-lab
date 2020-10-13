package sd.lab.concurrency.exercise;

import java.math.BigInteger;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class AsyncCalculatorImpl implements AsyncCalculator {

    private final ExecutorService executorService;

    AsyncCalculatorImpl(ExecutorService e) {
        this.executorService = Objects.requireNonNull(e);
    }

    @Override
    public CompletableFuture<BigInteger> factorial(BigInteger x) {
        final CompletableFuture<BigInteger> result = new CompletableFuture<>();
        if (x.signum() < 0) {
            executorService.execute(() ->
                    result.completeExceptionally(new IllegalArgumentException("Cannot compute factorial for negative numbers"))
            );
        } else {
            executorService.execute(() ->
                    factorial(BigInteger.ONE, BigInteger.ONE, x, result)
            );
        }
        return result;
    }

    private void factorial(BigInteger i, BigInteger x, BigInteger limit, CompletableFuture<BigInteger> result) {
        if (i.compareTo(limit) <= 0) {
            executorService.execute(() ->
                    factorial(
                            i.add(BigInteger.ONE),
                            x.multiply(i),
                            limit,
                            result
                    )
            );
        } else {
            result.complete(x);
        }
    }
}
