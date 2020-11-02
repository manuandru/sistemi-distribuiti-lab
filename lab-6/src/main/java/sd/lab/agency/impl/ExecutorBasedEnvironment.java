package sd.lab.agency.impl;

import sd.lab.agency.fsm.AgentFSM;
import sd.lab.agency.fsm.impl.ExecutorBasedAgentFSM;

import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class ExecutorBasedEnvironment<A extends ExecutorBasedAgentFSM> extends AbstractEnvironment<A> {

    private final ExecutorService executorService;

    public ExecutorBasedEnvironment(String name, ExecutorService executorService) {
        super(name);
        this.executorService = Objects.requireNonNull(executorService);
    }

    @Override
    protected ExecutorService getExecutorService() {
        return executorService;
    }

    @Override
    protected void onNewAgentRegistering(AgentFSM agent) {
        if (!(agent instanceof ExecutorBasedAgentFSM)) {
            throw new IllegalArgumentException("The provided agent is not an instance of " + ExecutorBasedAgentFSM.class);
        }
        ((ExecutorBasedAgentFSM) agent).setExecutorService(executorService);
    }
}
