package sd.lab.agency.fsm.impl;

import java.util.concurrent.Semaphore;

public abstract class ThreadBasedAgentFSM extends AbstractAgentFSM {

    private final Semaphore pauseMutex = new Semaphore(0);

    private final Thread thread = new Thread(() -> {
        try {
            while (getAgentState() != null) {
                doStateTransition(getNextOperation());
                if (getAgentState() == State.PAUSED) {
                    pauseMutex.acquire();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    });

    protected ThreadBasedAgentFSM(String name) {
        super(name);
    }

    @Override
    protected final void doBegin() {
        ensureCurrentStateIs(State.STARTED);
        begin();
    }

    @Override
    protected final void doRun() {
        ensureCurrentStateIsOneOf(State.STARTED, State.RUNNING, State.PAUSED, State.STOPPED);
        run();
    }

    @Override
    protected final void doEnd() {
        ensureCurrentStateIsOneOf(State.STARTED, State.RUNNING, State.PAUSED, State.STOPPED);
        end();
    }

    @Override
    public void onBegin() throws Exception {
        // it's a callback and it does nothing by default (must be inherited)
    }

    // TODO keep this abstract in order to force subclasses to provide some behaviour
    public abstract void onRun() throws Exception;

    @Override
    public void onEnd() throws Exception {
        // does nothing by default
    }

    @Override
    public final void start() {
        ensureCurrentStateIs(State.CREATED);
        setNextOperation(Operation.CONTINUE);
        thread.start();
    }

    @Override
    public final void resume() {
        ensureCurrentStateIs(State.PAUSED);
        setNextOperation(Operation.CONTINUE);
        pauseMutex.release();
    }

    @Override
    public final void pause() {
        ensureCurrentStateIsOneOf(State.STARTED, State.RUNNING, State.PAUSED);
        setNextOperation(Operation.PAUSE);
    }

    @Override
    public final void stop() {
        ensureCurrentStateIsOneOf(State.STARTED, State.RUNNING, State.PAUSED, State.STOPPED);
        setNextOperation(Operation.STOP);
        if (currentStateIs(State.PAUSED)) {
            pauseMutex.release();
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
            resume();
        }
    }

    @Override
    protected void afterStep() { }
}

