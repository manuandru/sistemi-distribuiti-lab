package sd.lab.agency.behaviour.impl;

import sd.lab.linda.textual.TextualSpace;

import java.util.concurrent.CompletableFuture;

public abstract class Count extends LindaOperation<Integer> {
    @Override
    public abstract String getTextualSpaceName();

    @Override
    public CompletableFuture<Integer> invokeOperation(TextualSpace textualSpace) {
        return textualSpace.count();
    }
}
