package sd.lab.agency;

import sd.lab.agency.fsm.AgentFSM;
import sd.lab.agency.fsm.impl.*;
import sd.lab.agency.impl.ExecutorBasedEnvironment;
import sd.lab.agency.impl.MultiThreadedEnvironment;
import sd.lab.linda.textual.TextualSpace;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.*;

public interface Environment<A extends AgentFSM> {

    A createAgent(Class<A> clazz, String name, Object... args);

    A registerAgent(A agent);

    Set<AID> getAgents();

    String getName();

    TextualSpace getTextualSpace(String name);

    void awaitAllAgents(Duration duration) throws InterruptedException, ExecutionException, TimeoutException;

    AID aidOf(String localOrFullName);

    static <A extends ThreadBasedAgentFSM> Environment<A> multiThreaded(String name) {
        return new MultiThreadedEnvironment(name);
    }

    static <A extends ThreadBasedAgentFSM> Environment<A> multiThreaded() {
        return multiThreaded(null);
    }

    static <A extends ExecutorBasedAgentFSM> Environment<A> executorBased(String name, ExecutorService executorService) {
        return new ExecutorBasedEnvironment(name, executorService);
    }

    static <A extends ExecutorBasedAgentFSM> Environment<A> executorBased(String name) {
        return executorBased(name, Executors.newSingleThreadExecutor());
    }

    static <A extends ExecutorBasedAgentFSM> Environment<A> executorBased(ExecutorService executorService) {
        return executorBased(null, executorService);
    }

    static <A extends ExecutorBasedAgentFSM> Environment<A> executorBased() {
        return executorBased((String)null);
    }

}
