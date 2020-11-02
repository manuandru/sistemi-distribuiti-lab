package sd.lab.agency.behaviour.impl;

import sd.lab.agency.Agent;
import sd.lab.linda.textual.StringTuple;
import sd.lab.linda.textual.TextualSpace;

import java.util.concurrent.CompletableFuture;

public abstract class LindaOperation extends AwaitPromise<StringTuple> {

    public abstract String getTextualSpaceName();

    public abstract CompletableFuture<StringTuple> invokeOperation(TextualSpace textualSpace);

    @Override
    public CompletableFuture<StringTuple> invokeAsync(Agent agent) {
        TextualSpace textualSpace = agent.getEnvironment().getTextualSpace(getTextualSpaceName());
        return invokeOperation(textualSpace);
    }
}
