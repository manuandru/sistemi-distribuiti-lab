package sd.lab.agency.behaviour.impl;

import sd.lab.agency.AID;
import sd.lab.agency.Agent;
import sd.lab.linda.textual.RegexTemplate;
import sd.lab.linda.textual.StringTuple;
import sd.lab.linda.textual.TextualSpace;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public abstract class Receive extends LindaOperation<StringTuple> {
    private final String payloadRegex;
    private final String senderRegex;
    private AID receiver;

    protected Receive(String senderRegex, String payloadRegex) {
        this.senderRegex = Objects.requireNonNull(senderRegex);
        this.payloadRegex = Objects.requireNonNull(payloadRegex);
    }

    @Override
    public String getTextualSpaceName() {
        return "inbox-" + receiver.getEnvironmentName() + "-" + receiver.getLocalName();
    }

    @Override
    public CompletableFuture<StringTuple> invokeOperation(TextualSpace textualSpace) {
        return textualSpace.in(messageTemplate());
    }

    private RegexTemplate messageTemplate() {
        return RegexTemplate.of(messageRegex());
    }

    private String messageRegex() {
        return String.format("^message from: (%s), to: (%s), content: (%s)$", senderRegex, Pattern.quote(receiver.toString()), payloadRegex);
    }

    @Override
    public void onOperationResult(Agent agent, StringTuple result) throws Exception {
        agent.log("Received %s", result.getValue());
        var messageRegex = Pattern.compile(messageRegex());
        var match = messageRegex.matcher(result.getValue());
        if (match.find()) {
            var sender = match.group(1);
            var payload = match.group(3);
            onMessageReceived(agent, AID.parse(sender), payload);
        } else {
            agent.log("Malformed message tuple: %s", result);
        }
    }

    public abstract void onMessageReceived(Agent receiver, AID sender, String payload);

    @Override
    public CompletableFuture<StringTuple> invokeAsync(Agent agent) {
        receiver = agent.getAID();
        return super.invokeAsync(agent);
    }

    @Override
    protected String getOperationName() {
        return "in";
    }
}
