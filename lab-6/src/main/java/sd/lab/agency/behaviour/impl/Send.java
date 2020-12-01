package sd.lab.agency.behaviour.impl;

import sd.lab.agency.AID;
import sd.lab.agency.Agent;
import sd.lab.agency.behaviour.Behaviour;
import sd.lab.linda.textual.StringTuple;
import sd.lab.linda.textual.TextualSpace;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class Send extends LindaOperation<StringTuple> {
    private final AID receiver;
    private final String payload;
    private AID sender;

    public Send(AID receiver, String payload) {
        this.receiver = Objects.requireNonNull(receiver);
        this.payload = Objects.requireNonNull(payload);
    }

    @Override
    public String getTextualSpaceName() {
        return "inbox-" + receiver.getEnvironmentName() + "-" + receiver.getLocalName();
    }

    @Override
    public CompletableFuture<StringTuple> invokeOperation(TextualSpace textualSpace) {
        return textualSpace.out(tupleMessage());
    }

    private StringTuple tupleMessage() {
        return StringTuple.of(
                String.format("message from: %s, to: %s, content: %s", sender, receiver, payload)
        );
    }

    @Override
    public void onResult(Agent agent, StringTuple result) throws Exception {
        agent.log("Dispatched %s", result.getValue());
    }

    @Override
    public CompletableFuture<StringTuple> invokeAsync(Agent agent) {
        sender = agent.getAID();
        return super.invokeAsync(agent);
    }

    @Override
    public Behaviour deepClone() {
        return new Send(receiver, payload);
    }
}
