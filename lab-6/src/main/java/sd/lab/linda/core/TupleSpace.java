package sd.lab.linda.core;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.apache.commons.collections4.MultiSet;

public interface TupleSpace<T extends Tuple, TT extends Template> {
    CompletableFuture<T> rd(TT template);

    CompletableFuture<T> in(TT template);

    CompletableFuture<T> out(T tuple);

    CompletableFuture<MultiSet<? extends T>> get();

    CompletableFuture<Integer> getSize();
    
    String getName();
}