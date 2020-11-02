package sd.lab.agency.fsm.impl;

import java.util.Objects;
import java.util.concurrent.*;

public abstract class ExecutorBasedAgentFSM extends AbstractAgentFSM {

    private ExecutorService executorService;

    protected ExecutorBasedAgentFSM(String name) {
        super(name);
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public final void setExecutorService(ExecutorService executorService) {
        ensureCurrentStateIs(State.CREATED);
        if (this.executorService == null) {
            this.executorService = Objects.requireNonNull(executorService);
        }
    }

    protected final void doBegin() {
        ensureCurrentStateIs(State.STARTED);
        getExecutorService().submit(this::begin);
    }

    @Override
    public void onBegin() throws Exception {
        // it's a callback and it does nothing by default (must be inherited)
    }

    protected final void doRun() {
        ensureCurrentStateIsOneOf(State.PAUSED, State.RUNNING);
        getExecutorService().submit(this::run);
    }

    // TODO keep this abstract in order to force subclasses to provide some behaviour
    public abstract void onRun() throws Exception;

    protected final void doEnd() {
        ensureCurrentStateIsOneOf(State.STARTED, State.PAUSED, State.RUNNING, State.STOPPED);
        getExecutorService().submit(this::end);
    }

    @Override
    public void onEnd() throws Exception {
        // does nothing by default
    }

    @Override
    public final void start() {
        ensureCurrentStateIs(State.CREATED);
        doStateTransition(Operation.CONTINUE);
    }

    @Override
    public final void resume() {
        ensureCurrentStateIs(State.PAUSED);
        doStateTransition(Operation.CONTINUE);
    }

    @Override
    public final void pause() {
        ensureCurrentStateIsOneOf(State.STARTED, State.RUNNING, State.PAUSED);
        setNextOperation(Operation.PAUSE);
    }

    @Override
    public final void stop() {
        ensureCurrentStateIsOneOf(State.STARTED, State.RUNNING, State.PAUSED);
        if (currentStateIs(State.PAUSED)) {
            doStateTransition(Operation.STOP);
        } else {
            setNextOperation(Operation.STOP);
        }
    }

    @Override
    public final void restart() {
        ensureCurrentStateIsOneOf(State.STARTED, State.RUNNING, State.PAUSED, State.STOPPED);
        setNextOperation(Operation.RESTART);
    }

    @Override
    public final void resumeIfPaused() {
        if (currentStateIs(State.PAUSED)) {
            doStateTransition(Operation.CONTINUE);
        }
    }

    @Override
    protected void afterStep() {
        doStateTransition(getNextOperation());
    }
}

