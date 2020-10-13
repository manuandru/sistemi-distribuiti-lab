package sd.lab.concurrency.exercise;

import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * Computes factorial asynchronously
 *
 * TODO implement this interface
 */
public interface AsyncFactorialCalculator {

    /**
     * Shortcut for <code>factorial(BigInteger.valueOf(x))</code>
     * @param x is the <code>long</code> value for which factorial should be computed
     * @return a {@link CompletableFuture} which allows developers to retrieve the factorial of <code>x</code> when it
     * is ready or wait for it if it is not.
     * The {@link CompletableFuture} is completed exceptionally if <code>x</code> is negative.
     */
    default CompletableFuture<BigInteger> factorial(long x) {
        return factorial(BigInteger.valueOf(x));
    }

    /**
     * Computes the factorial of <code>x</code>, asynchronously
     *
     * @param x is the {@link BigInteger} value for which factorial should be computed
     * @return a {@link CompletableFuture} which allows developers to retrieve the factorial of <code>x</code> when it
     * is ready or wait for it if it is not.
     * The {@link CompletableFuture} is completed exceptionally if <code>x</code> is negative.
     */
    CompletableFuture<BigInteger> factorial(BigInteger x);

    /**
     * Creates a new instance of <code>AsyncCalculator</code> out of an {@link ExecutorService} to be used behind the
     * scenes to perform asynchronous computations
     *
     * @param executorService a non-null {@link ExecutorService}
     * @return a new instance of {@link AsyncFactorialCalculator}
     */
    static AsyncFactorialCalculator newInstance(ExecutorService executorService) {
        return new AsyncFactorialCalculatorImpl(executorService);
    }
}
