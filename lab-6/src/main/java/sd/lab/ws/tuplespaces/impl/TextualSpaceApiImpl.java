package sd.lab.ws.tuplespaces.impl;

import org.apache.commons.collections4.MultiSet;
import sd.lab.linda.textual.RegexTemplate;
import sd.lab.linda.textual.StringTuple;
import sd.lab.linda.textual.TextualSpace;
import sd.lab.ws.tuplespaces.TextualSpaceApi;
import sd.lab.ws.tuplespaces.TextualSpaceStorage;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class TextualSpaceApiImpl implements TextualSpaceApi {

    private final TextualSpaceStorage storage;

    public TextualSpaceApiImpl(TextualSpaceStorage storage) {
        this.storage = Objects.requireNonNull(storage);
    }

    @Override
    public TextualSpaceStorage storage() {
        return storage;
    }

    @Override
    public CompletableFuture<Collection<? extends String>> getAllNames(int skip, int limit, String filter) {
        return CompletableFuture.completedFuture(
                storage.getAll().stream()
                        .skip(skip)
                        .limit(limit)
                        .map(TextualSpace::getName)
                        .filter(it -> it.contains(filter))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public CompletableFuture<StringTuple> insertTuple(String tupleSpaceName, StringTuple tuple) {
        return storage().getByName(tupleSpaceName).out(tuple);
    }

    @Override
    public CompletableFuture<StringTuple> readTuple(String tupleSpaceName, RegexTemplate template) {
        return storage().getByName(tupleSpaceName).rd(template);
    }

    @Override
    public CompletableFuture<StringTuple> consumeTuple(String tupleSpaceName, RegexTemplate template) {
        return storage().getByName(tupleSpaceName).in(template);
    }

    @Override
    public CompletableFuture<Integer> countTuples(String tupleSpaceName) {
        return storage().getByName(tupleSpaceName).count();
    }

    @Override
    public CompletableFuture<MultiSet<? extends StringTuple>> getAllTuples(String tupleSpaceName) {
        return storage().getByName(tupleSpaceName).get();
    }
}
