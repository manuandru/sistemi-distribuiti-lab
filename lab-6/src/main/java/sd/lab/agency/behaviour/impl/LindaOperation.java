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
        agent.log("Invoking %s operation on tuple space %s", getOperationName(), textualSpace.getName());
        return invokeOperation(textualSpace);
    }

    protected abstract String getOperationName();

    @Override
    public final void onResult(Agent agent, T result) throws Exception {
        agent.log("Completed %s operation on tuple space %s", getOperationName(), getTextualSpaceName());
        onOperationResult(agent, result);
    }

    public abstract void onOperationResult(Agent agent, T result) throws Exception;
}
