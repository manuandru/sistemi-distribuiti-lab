package sd.lab.agency.impl;

import sd.lab.agency.behaviour.Behaviour;
import sd.lab.agency.Agent;
import sd.lab.agency.fsm.impl.ThreadBasedAgentFSM;

import java.util.*;
import java.util.concurrent.SynchronousQueue;

public abstract class AbstractAgent extends ThreadBasedAgentFSM implements Agent {

    private final Queue<Behaviour> toDoList = new LinkedList<>();
    private final Set<Behaviour> toBeRemoved = new HashSet<>();

    protected AbstractAgent(String name) {
        super(name);
    }

    @Override
    public Queue<Behaviour> getToDoList() {
        return toDoList;
    }

    @Override
    public final void onBegin() {
        setup();
    }

    @Override
    public final void onRun() throws Exception {
        if (toDoList.isEmpty()) {
            pause();
        } else {
            final Queue<Behaviour> skipped = new LinkedList<>();
            Behaviour behaviour = toDoList.poll();
            try {
                while (behaviour != null && behaviour.isPaused()) {
                    skipped.add(behaviour);
                    behaviour = toDoList.poll();
                }
                toBeRemoved.clear();  // TODO notice this!
                if (behaviour != null) {
                    behaviour.execute(this);
                } else {
                    pause();
                }
            } finally {
                if (behaviour != null && !behaviour.isOver()) {
                    toDoList.add(behaviour);
                }
                toDoList.addAll(skipped);
                toDoList.removeAll(toBeRemoved);  // TODO notice this!
            }
        }
    }

    @Override
    public final void onEnd() {
        tearDown();
    }

    @Override
    public void setup() {
        // does nothing by default
    }

    @Override
    public void tearDown() {
        // does nothing by default
    }

    @Override
    public Agent addBehaviour(Collection<? extends Behaviour> behaviours) {
        if (behaviours.size() > 0) {
            toDoList.addAll(behaviours);
            resumeIfPaused();
        }
        return this;
    }

    @Override
    public Agent removeBehaviour(Collection<? extends Behaviour> behaviours) {
        if (behaviours.size() > 0) {
            toDoList.removeAll(behaviours);
            toBeRemoved.addAll(behaviours); // TODO notice this!
            resumeIfPaused();
        }
        return this;
    }
}
