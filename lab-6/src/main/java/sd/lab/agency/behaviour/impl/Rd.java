package sd.lab.agency.behaviour.impl;

import sd.lab.linda.textual.RegexTemplate;
import sd.lab.linda.textual.StringTuple;
import sd.lab.linda.textual.TextualSpace;

import java.util.concurrent.CompletableFuture;

public abstract class Rd extends LindaOperation<StringTuple> {
    @Override
    public abstract String getTextualSpaceName();

    public abstract RegexTemplate getTemplate();

    @Override
    public CompletableFuture<StringTuple> invokeOperation(TextualSpace textualSpace) {
        return textualSpace.rd(getTemplate());
    }

    @Override
    protected String getOperationName() {
        return "rd";
    }
}
