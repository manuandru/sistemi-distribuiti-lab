package sd.lab.agency.behaviour.impl;


import sd.lab.agency.behaviour.Behaviour;
import sd.lab.agency.Agent;

import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class Sequence implements Behaviour {

    private final Deque<Behaviour> subBehaviours = new LinkedList<>();

    public Sequence(Collection<Behaviour> bs) {
        if (bs.isEmpty()) throw new IllegalArgumentException();
        subBehaviours.addAll(bs);
    }

    public Sequence(Behaviour b, Behaviour... bs) {
        subBehaviours.add(b);
        subBehaviours.addAll(Arrays.asList(bs));
    }

    public Sequence(Behaviour b1, Behaviour b2, Behaviour... bs) {
        subBehaviours.add(b1);
        subBehaviours.add(b2);
        subBehaviours.addAll(Arrays.asList(bs));
    }

    @Override
    public Behaviour deepClone() {
        return new Sequence(subBehaviours.stream().map(Behaviour::deepClone).collect(Collectors.toList()));
    }

    @Override
    public boolean isPaused() {
        Behaviour nextBehaviour = subBehaviours.peek();
        return nextBehaviour != null && nextBehaviour.isPaused();
    }

    @Override
    public void execute(Agent agent) throws Exception {
        final Behaviour b = subBehaviours.poll();
        try {
            if (b != null) {
                b.execute(agent);
            }
        } finally {
            if (b != null && !b.isOver()) {
                subBehaviours.addFirst(b);
            }
        }
    }

    protected Deque<Behaviour> getSubBehaviours() {
        return subBehaviours;
    }

    @Override
    public boolean isOver() {
        return subBehaviours.isEmpty();
    }

}

