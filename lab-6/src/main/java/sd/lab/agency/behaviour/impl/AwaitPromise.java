package sd.lab.agency.behaviour.impl;

import sd.lab.agency.behaviour.Behaviour;
import sd.lab.agency.Agent;

import java.util.concurrent.CompletableFuture;

public abstract class AwaitPromise<T> implements Behaviour {

    private enum Phase { CREATED, PAUSED, COMPLETED, DONE }

    private Phase phase = Phase.CREATED;
    private CompletableFuture<T> promise;

    @Override
    public void execute(Agent agent) throws Exception {
        if (phase == Phase.CREATED) {
            promise = invokeAsync(agent);
            promise.thenRunAsync(() -> {
                phase = Phase.COMPLETED;
                agent.resumeIfPaused();
            });
            phase = Phase.PAUSED;
        } else if (phase == Phase.COMPLETED) {
            onResult(agent, promise.get());
            phase = Phase.DONE;
        } else {
            throw new IllegalStateException("This should never happen");
        }
    }

    public abstract void onResult(Agent agent, T result) throws Exception;

    public abstract CompletableFuture<T> invokeAsync(Agent agent);

    @Override
    public boolean isPaused() {
        return phase == Phase.PAUSED;
    }

    @Override
    public boolean isOver() {
        return phase == Phase.DONE;
    }

    @Override
    public Behaviour deepClone() {
        throw new IllegalStateException("You must override the deepClone method in class " + this.getClass().getName());
    }
}
