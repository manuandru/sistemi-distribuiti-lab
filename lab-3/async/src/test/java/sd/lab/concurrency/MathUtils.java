package sd.lab.concurrency;

import java.math.BigInteger;

/**
 * Utility methods for computing factorials in a synchronous way.
 * This is just here for enabling the automatic testing of your exercises.
 */
public class MathUtils {
    public static BigInteger factorial(long value) {
        return factorial(BigInteger.valueOf(value));
    }

    public static BigInteger factorial(final BigInteger limit) {
        final int sign = limit.signum();

        if (sign < 0) {
            throw new IllegalArgumentException("Cannot compute factorial for negative numbers");
        } else if (sign == 0) {
            return BigInteger.ONE;
        } else {
            BigInteger x = BigInteger.ONE;
            for (BigInteger i = BigInteger.ONE; i.compareTo(limit) <= 0; i = i.add(BigInteger.ONE)) {
                x = x.multiply(i);
            }
            return x;
        }
    }
}
