package sd.lab.agency.behaviour.impl;

import sd.lab.agency.Agent;
import sd.lab.agency.behaviour.Behaviour;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Collectors;

public class Parallel implements Behaviour {

    private final LinkedList<Behaviour> subBehaviours = new LinkedList<>();
    private final TerminationCriterion terminationCriterion;
    private boolean shortCircuitEnd = false;

    public Parallel(TerminationCriterion terminationCriterion, Collection<Behaviour> bs) {
        this.terminationCriterion = terminationCriterion;
        if (bs.isEmpty()) throw new IllegalArgumentException();
        subBehaviours.addAll(bs);
    }

    public Parallel(TerminationCriterion terminationCriterion, Behaviour b, Behaviour... bs) {
        this.terminationCriterion = terminationCriterion;
        subBehaviours.add(b);
        subBehaviours.addAll(Arrays.asList(bs));
    }

    @Override
    public Behaviour deepClone() {
        return new Parallel(terminationCriterion, subBehaviours.stream().map(Behaviour::deepClone).collect(Collectors.toList()));
    }

    @Override
    public void execute(Agent agent) throws Exception {
        final Queue<Behaviour> skipped = new LinkedList<>();
        Behaviour behaviour = subBehaviours.poll();
        try {
            while (behaviour != null && behaviour.isPaused()) {
                skipped.add(behaviour);
                behaviour = subBehaviours.poll();
            }

            if (behaviour != null) {
                behaviour.execute(agent);
            }
        } finally {
            if (behaviour != null && !behaviour.isOver()) {
                subBehaviours.addLast(behaviour);
            } else if (terminationCriterion == TerminationCriterion.ANY) {
                shortCircuitEnd = true;
            }
            subBehaviours.addAll(skipped);
        }
    }

    @Override
    public boolean isOver() {
        if (terminationCriterion == TerminationCriterion.ALL) {
            return subBehaviours.isEmpty();
        } else {
            return shortCircuitEnd || subBehaviours.isEmpty();
        }
    }

    @Override
    public boolean isPaused() {
        return subBehaviours.stream().allMatch(Behaviour::isPaused);
    }

    public enum TerminationCriterion {ANY, ALL}

}
