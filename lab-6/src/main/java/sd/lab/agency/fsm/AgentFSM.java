package sd.lab.agency.fsm;

import sd.lab.agency.AID;
import sd.lab.agency.Environment;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface AgentFSM {
    void setAid(AID aid);

    void onBegin() throws Exception;
    void onRun() throws Exception;
    void onEnd() throws Exception;

    void onUncaughtError(Exception e);

    void start();
    void stop();
    void pause();
    void resume();
    void restart();
    void resumeIfPaused();

    AID getAID();

    void await(Duration duration) throws InterruptedException, ExecutionException, TimeoutException;
    void await() throws InterruptedException, ExecutionException;

    Environment<?> getEnvironment();
    void setEnvironment(Environment<?> environment);

    void log(Object format, Object... args);
}
