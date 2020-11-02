package sd.lab.agency;

import sd.lab.agency.behaviour.Behaviour;
import sd.lab.agency.fsm.AgentFSM;

import java.util.Arrays;
import java.util.Collection;
import java.util.Queue;

public interface Agent extends AgentFSM {
    Queue<Behaviour> getToDoList();

    void setup();
    void tearDown();

    Agent addBehaviour(Collection<? extends Behaviour> behaviours);

    default Agent addBehaviour(Behaviour... behaviours) {
        return addBehaviour(Arrays.asList(behaviours));
    }

    Agent removeBehaviour(Collection<? extends Behaviour> behaviours);

    default Agent removeBehaviour(Behaviour... behaviours) {
        return removeBehaviour(Arrays.asList(behaviours));
    }

}
