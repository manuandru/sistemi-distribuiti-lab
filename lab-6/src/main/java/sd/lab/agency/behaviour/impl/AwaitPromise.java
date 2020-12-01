package sd.lab.agency.behaviour.impl;

import sd.lab.agency.Agent;
import sd.lab.agency.behaviour.Behaviour;

import java.util.concurrent.CompletableFuture;

public abstract class AwaitPromise<T> implements Behaviour {

    private enum Phase {CREATED, PAUSED, COMPLETED, DONE}

    private volatile Phase phase = Phase.CREATED;
    private volatile CompletableFuture<T> promise;

    @Override
    public synchronized void execute(Agent agent) throws Exception {
        if (phase == Phase.CREATED) {
            promise = invokeAsync(agent);
            promise.thenRunAsync(() -> {
                synchronized (this) {
                    phase = Phase.COMPLETED;
                    agent.log("Resuming behaviour %s", this);
                    agent.resumeIfPaused();
                }
            });
            phase = Phase.PAUSED;
            agent.log("Suspending behaviour %s", this);
        } else if (phase == Phase.COMPLETED) {
            onResult(agent, promise.get());
            phase = Phase.DONE;
            agent.log("Resumed behaviour %s", this);
        } else {
            throw new IllegalStateException("This should never happen");
        }
    }

    public abstract void onResult(Agent agent, T result) throws Exception;

    public abstract CompletableFuture<T> invokeAsync(Agent agent);

    @Override
    public synchronized boolean isPaused() {
        return phase == Phase.PAUSED;
    }

    @Override
    public synchronized boolean isOver() {
        return phase == Phase.DONE;
    }

    @Override
    public Behaviour deepClone() {
        throw new IllegalStateException("You must override the deepClone method in class " + this.getClass().getName());
    }
}
