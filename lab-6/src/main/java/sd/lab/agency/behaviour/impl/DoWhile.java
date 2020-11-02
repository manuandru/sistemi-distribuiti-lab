package sd.lab.agency.behaviour.impl;

import sd.lab.agency.behaviour.Behaviour;
import sd.lab.agency.Agent;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DoWhile extends Sequence {

    private final Deque<Behaviour> behavioursBackup;
    private boolean isEndOfRound = false;

    public DoWhile(Behaviour b, Behaviour... bs) {
        super(b, bs);
        behavioursBackup = getSubBehaviours().stream().map(Behaviour::deepClone).collect(Collectors.toCollection(LinkedList::new));
    }

    public DoWhile(Collection<Behaviour> bs) {
        super(bs);
        behavioursBackup = getSubBehaviours().stream().map(Behaviour::deepClone).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public Behaviour deepClone() {
        return new DoWhile(getSubBehaviours().stream().map(Behaviour::deepClone).collect(Collectors.toList()));
    }

    @Override
    public void execute(Agent agent) throws Exception {
        isEndOfRound = false;
        super.execute(agent);
        if (getSubBehaviours().size() == 0) {
            isEndOfRound = true;
            getSubBehaviours().addAll(behavioursBackup.stream().map(Behaviour::deepClone).collect(Collectors.toList()));
        }
    }

    @Override
    public boolean isOver() {
        return isEndOfRound && !condition();
    }

    public boolean condition() {
        return true;
    }

    public static DoWhile of(Behaviour step, Supplier<Boolean> condition) {
        return new DoWhile(step) {
            @Override
            public boolean condition() {
                return condition.get();
            }
        };
    }
}
