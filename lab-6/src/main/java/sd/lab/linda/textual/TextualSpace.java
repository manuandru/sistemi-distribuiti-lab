package sd.lab.linda.textual;

import sd.lab.linda.core.TupleSpace;
import sd.lab.linda.textual.impl.TextualSpaceImpl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public interface TextualSpace extends TupleSpace<StringTuple, RegexTemplate> {

    default CompletableFuture<StringTuple> rd(String regex) {
        return rd(RegexTemplate.of(regex));
    }

    default CompletableFuture<StringTuple> in(String regex) {
        return in(RegexTemplate.of(regex));
    }

    default CompletableFuture<StringTuple> out(String string) {
        return out(StringTuple.of(string));
    }

    static TextualSpace of(String name, ExecutorService engine) {
        return new TextualSpaceImpl(name, engine);
    }

    static TextualSpace of(ExecutorService engine) {
        return new TextualSpaceImpl(engine);
    }
}
