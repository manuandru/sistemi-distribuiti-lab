package sd.lab.concurrency.exercise;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class SingleThreadedExecutorService implements ExecutorService {

    private volatile boolean shutdown = false;
    private final CompletableFuture<?> termination = new CompletableFuture<>();
    private final BlockingQueue<Runnable> tasks = new LinkedBlockingDeque<>();
    private final Thread backgroundThread = new Thread(this::backgroundThreadMain);

    public SingleThreadedExecutorService() {
        backgroundThread.start();
    }

    private void backgroundThreadMain() {
        try {
            while (!backgroundThread.isInterrupted()) {
                if (tasks.isEmpty() && shutdown) {
                    return;
                } else {
                    tasks.take().run();
                }
            }
        } catch (InterruptedException ignored) {
            // do nothing
        } finally {
            termination.complete(null);
        }
    }

    @Override
    public void shutdown() {
        shutdown = true;
    }

    @Override
    public List<Runnable> shutdownNow() {
        shutdown();
        backgroundThread.interrupt();
        var runnables = new ArrayList<Runnable>();
        tasks.drainTo(runnables);
        return runnables;
    }

    @Override
    public boolean isShutdown() {
        return shutdown;
    }

    @Override
    public boolean isTerminated() {
        return termination.isDone();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        try {
            termination.get(timeout, unit);
            return true;
        } catch (ExecutionException ignored) {
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        if (shutdown) throw new RejectedExecutionException();
        final CompletableFuture<T> result = new CompletableFuture<>();
        tasks.add(() -> {
            try {
                result.complete(task.call());
            } catch (Exception e) {
                result.completeExceptionally(e);
            }
        });
        return result;
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        if (shutdown) throw new RejectedExecutionException();
        final CompletableFuture<T> promise = new CompletableFuture<>();
        tasks.add(() -> {
            try {
                task.run();
                promise.complete(result);
            } catch (Exception e) {
                promise.completeExceptionally(e);
            }
        });
        return promise;
    }

    @Override
    public Future<?> submit(Runnable task) {
        return submit(task, null);
    }

    @Override
    public void execute(Runnable command) {
        submit(command);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        throw new Error("this must not be implemented");
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        throw new Error("this must not be implemented");
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        throw new Error("this must not be implemented");
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        throw new Error("this must not be implemented");
    }
}
