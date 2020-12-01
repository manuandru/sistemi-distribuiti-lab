package sd.lab.agency.behaviour.impl;

import sd.lab.agency.Agent;
import sd.lab.linda.textual.StringTuple;
import sd.lab.linda.textual.TextualSpace;

import java.util.concurrent.CompletableFuture;

public abstract class Out extends LindaOperation<StringTuple> {
    @Override
    public abstract String getTextualSpaceName();

    public abstract StringTuple getTuple();

    @Override
    public CompletableFuture<StringTuple> invokeOperation(TextualSpace textualSpace) {
        return textualSpace.out(getTuple());
    }

    @Override
    public void onOperationResult(Agent agent, StringTuple result) throws Exception {
        // does nothing by default
    }

    @Override
    protected String getOperationName() {
        return "out";
    }
}
