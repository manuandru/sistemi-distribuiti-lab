package sd.lab.agency.behaviour.impl;

import sd.lab.agency.Agent;
import sd.lab.linda.textual.TextualSpace;

import java.util.concurrent.CompletableFuture;

public abstract class LindaOperation<T> extends AwaitPromise<T> {

    public abstract String getTextualSpaceName();

    public abstract CompletableFuture<T> invokeOperation(TextualSpace textualSpace);

    @Override
    public CompletableFuture<T> invokeAsync(Agent agent) {
        TextualSpace textualSpace = agent.getEnvironment().getTextualSpace(getTextualSpaceName());
        return invokeOperation(textualSpace);
    }
}
