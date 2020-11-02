package sd.lab.agency.fsm.impl;

import sd.lab.agency.AID;
import sd.lab.agency.Environment;
import sd.lab.agency.fsm.AgentFSM;

import java.time.Duration;
import java.util.EnumSet;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

abstract class AbstractAgentFSM implements AgentFSM {
    private final CompletableFuture<Void> termination = new CompletableFuture<>();
    private AID aid;
    private Environment<?> environment;
    private volatile State state = State.CREATED;
    private volatile Operation nextOperation = null;

    protected AbstractAgentFSM(String name) {
        aid = AID.local(name);
    }

    protected final void ensureCurrentStateIs(State state) {
        ensureCurrentStateIsOneOf(state);
    }

    protected final void ensureCurrentStateIsIn(EnumSet<State> states) {
        if (!currentStateIsIn(states)) {
            final RuntimeException e = new IllegalStateException("Illegal state: " + this.state + ", expected: " + states);
            e.printStackTrace();
            throw e;
        }
    }

    protected final void ensureCurrentStateIsOneOf(State state, State... states) {
        ensureCurrentStateIsIn(EnumSet.of(state, states));
    }

    protected final boolean currentStateIs(State state) {
        return Objects.equals(this.state, state);
    }

    protected final boolean currentStateIsIn(EnumSet<State> states) {
        return states.contains(this.state);
    }

    protected final boolean currentStateIsOneOf(State state, State... states) {
        return currentStateIsIn(EnumSet.of(state, states));
    }

    protected synchronized State getAgentState() {
        return state;
    }

    @Override
    public final AID getAID() {
        return aid;
    }

    @Override
    public final void setAid(AID aid) {
        ensureCurrentStateIs(State.CREATED);
        this.aid = Objects.requireNonNull(aid);
    }

    public final Environment<?> getEnvironment() {
        return environment;
    }

    public final void setEnvironment(Environment<?> environment) {
        ensureCurrentStateIs(State.CREATED);
        if (this.environment == null) {
            this.environment = Objects.requireNonNull(environment);
        }
    }

    protected synchronized final void doStateTransition(Operation whatToDo) {
        switch (state) {
            case CREATED:
                doStateTransitionFromCreated(whatToDo);
                break;
            case STARTED:
                doStateTransitionFromStarted(whatToDo);
                break;
            case RUNNING:
                doStateTransitionFromRunning(whatToDo);
                break;
            case PAUSED:
                doStateTransitionFromPaused(whatToDo);
                break;
            case STOPPED:
                doStateTransitionFromStopped(whatToDo);
                break;
            default: throw new IllegalStateException("Illegal state: " + state);
        }
    }

    private void doStateTransitionFromCreated(Operation whatToDo){
        switch (whatToDo) {
            case CONTINUE:
                state = State.STARTED;
                doBegin();
                break;
            default: throw new IllegalArgumentException("Unexpected transition: " + state + " -" + whatToDo + "-> ???");
        }
    }

    private void doStateTransitionFromStarted(Operation whatToDo){
        doStateTransitionFromRunning(whatToDo);
    }

    private void doStateTransitionFromRunning(Operation whatToDo){
        switch (whatToDo) {
            case PAUSE:
                state = State.PAUSED;
                break;
            case RESTART:
                state = State.STARTED;
                doBegin();
                break;
            case STOP:
                state = State.STOPPED;
                doEnd();
                break;
            case CONTINUE:
                state = State.RUNNING;
                doRun();
                break;
            default: throw new IllegalArgumentException("Unexpected transition: " + state + " -" + whatToDo + "-> ???");
        }
    }

    private void doStateTransitionFromPaused(Operation whatToDo){
        doStateTransitionFromRunning(whatToDo);
    }

    private void doStateTransitionFromStopped(Operation whatToDo){
        switch (whatToDo) {
            case RESTART:
                state = State.STARTED;
                doBegin();
                break;
            case STOP:
            case CONTINUE:
                state = null;
                termination.complete(null);
                break;
            default: throw new IllegalArgumentException("Unexpected transition: " + state + " -" + whatToDo + "-> ???");
        }
    }

    protected abstract void doBegin();
    protected abstract void doRun();
    protected abstract void doEnd();

    @Override
    public void onUncaughtError(Exception e) {
        // by default, it simply print stacktrace and then stops the agent (can be overridden)
        e.printStackTrace();
    }

    protected synchronized final void begin() {
        nextOperation = Operation.CONTINUE;
        try {
            onBegin();
        } catch (Exception e) {
            nextOperation = Operation.CONTINUE;
            onUncaughtError(e);
        } finally {
            afterStep();
        }
    }

    protected abstract void afterStep();

    protected synchronized final void run() {
        nextOperation = Operation.CONTINUE;
        try {
            onRun();
        } catch (Exception e) {
            nextOperation = Operation.CONTINUE;
            onUncaughtError(e);
        } finally {
            afterStep();
        }
    }

    protected synchronized final void end() {
        nextOperation = Operation.CONTINUE;
        try {
            onEnd();
        } catch (Exception e) {
            nextOperation = Operation.CONTINUE;
            onUncaughtError(e);
        } finally {
            afterStep();
        }
    }

    public synchronized Operation getNextOperation() {
        return nextOperation;
    }

    public synchronized void setNextOperation(Operation nextOperation) {
        this.nextOperation = Objects.requireNonNull(nextOperation);
    }

    @Override
    public final void log(Object format, Object... args) {
        System.out.printf("[" + getAID() +"] " + format + "\n", args);
    }

    @Override
    public final void await(Duration duration) throws InterruptedException, ExecutionException, TimeoutException {
        termination.get(duration.toMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public final void await() throws InterruptedException, ExecutionException {
        termination.get();
    }

}