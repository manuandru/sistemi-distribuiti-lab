package sd.lab.agency.impl;

import sd.lab.agency.AID;
import sd.lab.agency.Environment;
import sd.lab.agency.fsm.AgentFSM;
import sd.lab.linda.textual.TextualSpace;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractEnvironment<A extends AgentFSM> implements Environment<A> {
    private final String name;
    private final Map<String, AgentFSM> agents = new HashMap<>();
    private final Map<String, TextualSpace> textualSpaces = new HashMap<>();

    public AbstractEnvironment(String name) {
        this.name = Optional.ofNullable(name).orElseGet(() -> getClass().getSimpleName() + "#" + System.identityHashCode(this));
    }

    public AbstractEnvironment() {
        this(null);
    }

    protected abstract ExecutorService getExecutorService();

    @Override
    public A createAgent(Class<A> agentClass, String name, Object... args) {
        if (agents.containsKey(name)) {
            throw new IllegalArgumentException(String.format("An agent named %s already exists in environment %s", name, getName()));
        }

        final Object[] arguments = Stream.concat(Stream.of(name), Stream.of(args)).toArray();

        final Optional<A> newAgent = Stream.of(agentClass.getConstructors())
                .filter(c -> c.getParameterCount() == arguments.length)
                .map(constructor -> {
                    constructor.setAccessible(true);
                    try {
                        @SuppressWarnings("unchecked")
                        final A agent = (A) constructor.newInstance(arguments);
                        return Optional.of(agent);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        throw new IllegalStateException(e);
                    }
                }).findAny()
                .flatMap(Function.identity());

        if (!newAgent.isPresent()) {
            throw new IllegalArgumentException("No constructor for class " + agentClass.getName() + " accepts arguments: " + Arrays.toString(arguments));
        }

        return registerAgent(newAgent.get());
    }

    @Override
    public TextualSpace getTextualSpace(String name) {
        synchronized (textualSpaces) {
            if (!textualSpaces.containsKey(name)) {
                textualSpaces.put(name, TextualSpace.of(name, getExecutorService()));
            }
            return textualSpaces.get(name);
        }
    }

    @Override
    public A registerAgent(A agent) {
        if (agents.containsKey(agent.getAID().getLocalName())) {
            throw new IllegalArgumentException(
                    String.format("An agent named %s already exists in environment %s", agent.getAID().getLocalName(), getName())
            );
        }
        onNewAgentRegistering(agent);
        agent.setEnvironment(this);
        agent.setAid(aidOf(agent.getAID().getLocalName()));
        agents.put(agent.getAID().getLocalName(), agent);
        return agent;
    }

    protected abstract void onNewAgentRegistering(AgentFSM agent);

    @Override
    public Set<AID> getAgents() {
        return agents.keySet().stream().map(this::aidOf).collect(Collectors.toSet());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void awaitAllAgents(Duration duration) throws InterruptedException, ExecutionException, TimeoutException {
        for (AgentFSM a : agents.values()) {
            a.await(duration);
        }
    }

    @Override
    public AID aidOf(String localOrFullName) {
        if (localOrFullName.contains("@")) {
            return AID.parse(localOrFullName);
        } else {
            return AID.full(localOrFullName, getName());
        }
    }
}
