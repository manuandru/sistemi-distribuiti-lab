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
            while (!tasks.isEmpty() || !shutdown) {
                // cannot throw - exception are caught inside the task
                tasks.take().run();
            }
        } catch (InterruptedException ignored) {
            // do nothing - exit from waiting the queue
        }  finally {
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
        unit.timedJoin(backgroundThread, timeout);
        return termination.isDone();
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        if (this.shutdown) {
            throw new RejectedExecutionException();
        }
        CompletableFuture<T> futureToReturn = new CompletableFuture<>();
        this.tasks.add(() -> {
            try {
                futureToReturn.complete(task.call());
            } catch (Exception e) {
                futureToReturn.completeExceptionally(e);
            }
        });
        return futureToReturn;
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return this.submit(() -> { task.run(); return result; } );
    }

    @Override
    public Future<?> submit(Runnable task) {
        return this.submit(task, null);
    }

    @Override
    public void execute(Runnable command) {
        this.submit(command);
    }

    // ignore the following methods: they are not tested

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
