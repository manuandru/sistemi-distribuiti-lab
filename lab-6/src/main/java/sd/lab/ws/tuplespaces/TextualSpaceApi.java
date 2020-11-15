package sd.lab.ws.tuplespaces;

import org.apache.commons.collections4.MultiSet;
import sd.lab.linda.textual.RegexTemplate;
import sd.lab.linda.textual.StringTuple;
import sd.lab.ws.tuplespaces.impl.TextualSpaceApiImpl;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface TextualSpaceApi {

    TextualSpaceStorage storage();

    CompletableFuture<Collection<? extends String>> getAllNames(int skip, int limit, String filter);

    CompletableFuture<StringTuple> insertTuple(String tupleSpaceName, StringTuple tuple);

    CompletableFuture<StringTuple> readTuple(String tupleSpaceName, RegexTemplate template);

    CompletableFuture<StringTuple> consumeTuple(String tupleSpaceName, RegexTemplate template);

    CompletableFuture<Integer> countTuples(String tupleSpaceName);

    CompletableFuture<MultiSet<? extends StringTuple>> getAllTuples(String tupleSpaceName);

    static TextualSpaceApi of(TextualSpaceStorage storage) {
        return new TextualSpaceApiImpl(storage);
    }
}
