package sd.lab.test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Assert;

public class ConcurrentTestHelper {
    
    private static final Duration BLOCKING_THRESHOLD = Duration.ofSeconds(3);
    private static final Duration GET_THRESHOLD = Duration.ofSeconds(1);
    private static final Duration MAX_WAIT_THRESHOLD = Duration.ofSeconds(10);


    @FunctionalInterface
    public interface ThrowableRunnable {
        void run() throws Exception;
    }
    
    private final List<ThrowableRunnable> toDoList = Collections.synchronizedList(new ArrayList<>());
    private CountDownLatch latch;
    
    public void setThreadCount(int n) {
    	this.latch = new CountDownLatch(n);
    }
    
    public void await() throws Exception {
        latch.await(MAX_WAIT_THRESHOLD.toMillis(), TimeUnit.MILLISECONDS);
        for (ThrowableRunnable throwableRunnable : toDoList) {
			throwableRunnable.run();
		}
    }
    
    public void done() {
        latch.countDown();
    }
    
    public void fail(Exception t) {
    	toDoList.add(() -> {
        	throw new AssertionError(t);
        });
    }
    
    public void fail(String message) {
        toDoList.add(() -> Assert.fail(message));
    }
    
    public void fail() {
        toDoList.add(() -> Assert.fail());

    }
    
    public void success() {
        toDoList.add(() -> Assert.assertTrue(true));
    }
    
    public void assertTrue(boolean condition) {
        toDoList.add(() -> Assert.assertTrue(condition));
    }
    
    public void assertTrue(boolean condition, String message) {
        toDoList.add(() -> Assert.assertTrue(message, condition));
    }
    
    public void assertEquals(Object actual, Object expected, String message) {
        assertTrue(expected.equals(actual), message);
    }
    
    public void assertEquals(Object actual, Object expected) {
        assertTrue(expected.equals(actual));
    }
    
    public <T> void assertEquals(Future<T> actualFuture, T expected) {
        try {
            final T actual = actualFuture.get(GET_THRESHOLD.toMillis(), TimeUnit.MILLISECONDS);
            assertEquals(actual, expected);
        }  catch (InterruptedException | ExecutionException | TimeoutException e) {
            fail(e);
        }         
    }
    
    public <T> void assertEquals(Future<T> actualFuture, T expected, String message) {
        try {
            final T actual = actualFuture.get(GET_THRESHOLD.toMillis(), TimeUnit.MILLISECONDS);
            assertEquals(actual, expected, message);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
        	fail(e);
        }       
    }
    
    public <T> void assertOneOf(Future<T> actualFuture, T expected1, @SuppressWarnings("unchecked") T... expected) {
        try {
            final T actual = actualFuture.get(GET_THRESHOLD.toMillis(), TimeUnit.MILLISECONDS);
            assertOneOf(actual, expected1, expected);
        }  catch (InterruptedException | ExecutionException | TimeoutException e) {
        	fail(e);
        }         
    }

    public <T> void assertOneOf(T actual, T expected1, @SuppressWarnings("unchecked") T... expected) {
        final Set<T> set = new HashSet<>(Arrays.asList(expected));
        set.add(expected1);
        assertTrue(set.contains(actual));
    }
    
    public void assertBlocksIndefinitely(Future<?> future, String message) {
        try {
            future.get(BLOCKING_THRESHOLD.toMillis(), TimeUnit.MILLISECONDS);
            fail(message);
        } catch (InterruptedException | ExecutionException e) {
        	fail(e);
        } catch (TimeoutException e) {
            success();
        }       
    }
    
    public void assertBlocksIndefinitely(Future<?> future) {
        try {
            future.get(BLOCKING_THRESHOLD.toMillis(), TimeUnit.MILLISECONDS);
            fail();
        } catch (InterruptedException | ExecutionException e) {
        	fail(e);
        } catch (TimeoutException e) {
            success();
        }       
    }
    
    public void assertEventuallyReturns(Future<?> future, String message) {
        try {
            future.get(BLOCKING_THRESHOLD.toMillis(), TimeUnit.MILLISECONDS);
            success();
        } catch (InterruptedException | ExecutionException e) {
        	fail(e);
        } catch (TimeoutException e) {
            fail(message);
        }       
    }
    
    public void assertEventuallyReturns(Future<?> future) {
        try {
            future.get(BLOCKING_THRESHOLD.toMillis(), TimeUnit.MILLISECONDS);
            success();
        } catch (InterruptedException | ExecutionException e) {
        	fail(e);
        } catch (TimeoutException e) {
            fail();
        }       
    }
    
}
