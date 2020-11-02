package sd.lab.agency.impl;

import sd.lab.agency.fsm.AgentFSM;
import sd.lab.agency.fsm.impl.ThreadBasedAgentFSM;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

public class MultiThreadedEnvironment<A extends ThreadBasedAgentFSM> extends AbstractEnvironment<A> {

    public MultiThreadedEnvironment(String name) {
        super(name);
    }

    @Override
    protected ExecutorService getExecutorService() {
        return ForkJoinPool.commonPool();
    }

    @Override
    protected void onNewAgentRegistering(AgentFSM agent) {
        if (!(agent instanceof ThreadBasedAgentFSM)) {
            throw new IllegalArgumentException("The provided agent is not an instance of " + ThreadBasedAgentFSM.class);
        }
    }
}
