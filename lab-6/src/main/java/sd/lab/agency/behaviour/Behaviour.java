package sd.lab.agency.behaviour;

import sd.lab.agency.Agent;
import sd.lab.agency.behaviour.impl.*;
import sd.lab.linda.textual.RegexTemplate;
import sd.lab.linda.textual.StringTuple;
import sd.lab.utils.Action;
import sd.lab.utils.Action1;

import java.time.Duration;
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

    static Behaviour wait(Duration duration) {
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
            public void onResult(Agent agent, StringTuple result) throws Exception {
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
            public void onResult(Agent agent, StringTuple result) throws Exception {
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
