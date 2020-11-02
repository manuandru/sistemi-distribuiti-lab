package sd.lab.agency.behaviour.impl;

import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.multiset.HashMultiSet;
import sd.lab.agency.Agent;
import sd.lab.linda.textual.StringTuple;
import sd.lab.linda.textual.TextualSpace;

import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

public abstract class Get extends LindaOperation<MultiSet<StringTuple>> {
    @Override
    public abstract String getTextualSpaceName();

    @Override
    public CompletableFuture<MultiSet<StringTuple>> invokeOperation(TextualSpace textualSpace) {
        return textualSpace.get().thenApply(HashMultiSet::new);
    }
}
