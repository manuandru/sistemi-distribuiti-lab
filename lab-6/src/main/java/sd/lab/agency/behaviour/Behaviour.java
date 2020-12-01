package sd.lab.agency.behaviour;

import org.apache.commons.collections4.MultiSet;
import sd.lab.agency.AID;
import sd.lab.agency.Agent;
import sd.lab.agency.behaviour.impl.*;
import sd.lab.agency.fsm.AgentFSM;
import sd.lab.linda.textual.RegexTemplate;
import sd.lab.linda.textual.StringTuple;
import sd.lab.utils.Action;
import sd.lab.utils.Action1;

import java.time.Duration;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

@FunctionalInterface
public interface Behaviour {

    /// ACTUAL INTERFACE

    void execute(Agent agent) throws Exception;

    default boolean isPaused() {
        return false;
    }

    default boolean isOver() {
        return true;
    }

    default Behaviour deepClone() {
        return this;
    }

    /// STATIC FACTORIES

    static Behaviour stopAgent() {
        return AgentFSM::stop;
    }

    static Behaviour of(Action<? extends Exception> action) {
        return agent -> action.execute();
    }

    static Behaviour of(Action1<Agent, ? extends Exception> action) {
        return action::execute;
    }

    static Behaviour sequence(Behaviour b, Behaviour... bs) {
        return new Sequence(b, bs);
    }

    static Behaviour allOf(Behaviour b, Behaviour... bs) {
        return new Parallel(Parallel.TerminationCriterion.ALL, b, bs);
    }

    static Behaviour anyOf(Behaviour b, Behaviour... bs) {
        return new Parallel(Parallel.TerminationCriterion.ANY, b, bs);
    }

    static Behaviour waitFor(Duration duration) {
        return new Wait(duration);
    }

    static Behaviour out(String tupleSpace, Supplier<StringTuple> tuple) {
        return new Out() {
            @Override
            public String getTextualSpaceName() {
                return tupleSpace;
            }

            @Override
            public StringTuple getTuple() {
                return tuple.get();
            }

            @Override
            public Behaviour deepClone() {
                return out(tupleSpace, tuple);
            }
        };
    }

    static Behaviour out(String tupleSpace, StringTuple tuple) {
        return out(tupleSpace, () -> tuple);
    }

    static Behaviour in(String tupleSpace, Supplier<RegexTemplate> template, Consumer<StringTuple> onTupleConsumed) {
        return new In() {
            @Override
            public String getTextualSpaceName() {
                return tupleSpace;
            }

            @Override
            public RegexTemplate getTemplate() {
                return template.get();
            }

            @Override
            public void onOperationResult(Agent agent, StringTuple result) throws Exception {
                onTupleConsumed.accept(result);
            }

            @Override
            public Behaviour deepClone() {
                return in(tupleSpace, template, onTupleConsumed);
            }
        };
    }

    static Behaviour in(String tupleSpace, RegexTemplate template, Consumer<StringTuple> onTupleConsumed) {
        return in(tupleSpace, () -> template, onTupleConsumed);
    }

    static Behaviour rd(String tupleSpace, Supplier<RegexTemplate> template, Consumer<StringTuple> onTupleRead) {
        return new Rd() {
            @Override
            public String getTextualSpaceName() {
                return tupleSpace;
            }

            @Override
            public RegexTemplate getTemplate() {
                return template.get();
            }

            @Override
            public void onOperationResult(Agent agent, StringTuple result) throws Exception {
                onTupleRead.accept(result);
            }

            @Override
            public Behaviour deepClone() {
                return rd(tupleSpace, template, onTupleRead);
            }
        };
    }

    static Behaviour rd(String tupleSpace, RegexTemplate template, Consumer<StringTuple> onTupleRead) {
        return rd(tupleSpace, () -> template, onTupleRead);
    }

    static Behaviour get(String tupleSpace, Consumer<MultiSet<StringTuple>> onTuplesRead) {
        return new Get() {
            @Override
            public String getTextualSpaceName() {
                return tupleSpace;
            }

            @Override
            public void onOperationResult(Agent agent, MultiSet<StringTuple> result) throws Exception {
                onTuplesRead.accept(result);
            }

            @Override
            public Behaviour deepClone() {
                return get(tupleSpace, onTuplesRead);
            }
        };
    }

    static Behaviour count(String tupleSpace, Consumer<Integer> onTuplesCounted) {
        return new Count() {
            @Override
            public String getTextualSpaceName() {
                return tupleSpace;
            }

            @Override
            public void onOperationResult(Agent agent, Integer result) throws Exception {
                onTuplesCounted.accept(result);
            }

            @Override
            public Behaviour deepClone() {
                return count(tupleSpace, onTuplesCounted);
            }
        };
    }

    static Behaviour send(AID receiver, String payload) {
        return new Send(receiver, payload);
    }

    static Behaviour receive(String senderRegex, String messagePayload, BiConsumer<AID, String> onMessageReceived) {
        return new Receive(senderRegex, messagePayload) {
            @Override
            public void onMessageReceived(Agent receiver, AID sender, String payload) {
                onMessageReceived.accept(sender, payload);
            }

            @Override
            public Behaviour deepClone() {
                return receive(senderRegex, messagePayload, onMessageReceived);
            }
        };
    }

    static Behaviour receiveFromAnyone(String messagePayload, BiConsumer<AID, String> onMessageReceived) {
        return receive(".*?", messagePayload, onMessageReceived);
    }

    static Behaviour receiveAnyMessageFromAnyone(BiConsumer<AID, String> onMessageReceived) {
        return receive(".*?", ".*", onMessageReceived);
    }

    static Behaviour receiveAnyMessage(String senderRegex, BiConsumer<AID, String> onMessageReceived) {
        return receive(senderRegex, ".*", onMessageReceived);
    }

    /// DEFAULT OPERATORS

    default Behaviour addTo(Agent agent) {
        agent.addBehaviour(this);
        return this;
    }

    default Behaviour removeFrom(Agent agent) {
        agent.removeBehaviour(this);
        return this;
    }

    default Behaviour andThen(Behaviour b, Behaviour... bs) {
        return new Sequence(this, b, bs);
    }

    default Behaviour andThen(Action<? extends Exception> action) {
        return andThen(Behaviour.of(action));
    }

    default Behaviour andThen(Action1<Agent, ? extends Exception> action) {
        return andThen(Behaviour.of(action));
    }

    default Behaviour repeatManyTimes(int times) {
        return new DoWhile(this) {
            private int i = 0;

            @Override
            public Behaviour deepClone() {
                return Behaviour.this.deepClone().repeatManyTimes(times);
            }

            @Override
            public boolean condition() {
                return ++i < times;
            }
        };
    }

    default Behaviour repeatWhile(Supplier<Boolean> condition) {
        return DoWhile.of(this, condition);
    }

    default Behaviour repeatUntil(Supplier<Boolean> condition) {
        return repeatWhile(() -> !condition.get());
    }

    default Behaviour repeatForEver() {
        return repeatWhile(() -> true);
    }
}
