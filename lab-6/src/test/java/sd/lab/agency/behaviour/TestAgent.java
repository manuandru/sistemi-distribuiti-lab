package sd.lab.agency.behaviour;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import sd.lab.agency.Environment;
import sd.lab.agency.fsm.AgentFSM;
import sd.lab.agency.impl.AbstractAgent;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RunWith(Parameterized.class)
public class TestAgent {

    private static final Duration MAX_WAIT = Duration.ofSeconds(3);
    private static final int TEST_REPETITIONS = 5;
    
    private final int testIndex;
    protected Environment<AbstractAgent> mas;


    public TestAgent(Integer i) {
        testIndex = i;
    }

    @Parameterized.Parameters
    public static Iterable<Integer> data() {
        return IntStream.range(0, TEST_REPETITIONS).boxed().collect(Collectors.toList());
    }

    @Before
    public void setUp() {
        mas = Environment.multiThreaded(TestAgent.class.getSimpleName() + "-Environment-" + testIndex);
    }

    @After
    public void tearDown() throws InterruptedException, TimeoutException, ExecutionException {
        mas.awaitAllAgents(MAX_WAIT);
    }

    @Test
    public void testOneShot() throws Exception {
        final List<Integer> xs = new LinkedList<>();

        mas.registerAgent(new AbstractAgent("testOneShot-" + testIndex) {
            @Override
            public void setup() {
                Behaviour.of(() -> xs.add(1))
                        .andThen(AgentFSM::stop)
                        .addTo(this);
            }
        }).start();

        mas.awaitAllAgents(MAX_WAIT);

        Assert.assertEquals(xs, Collections.singletonList(1));
    }

    @Test
    public void testSequence1() throws Exception {
        final List<Integer> xs = new LinkedList<>();

        mas.registerAgent(new AbstractAgent("testSequence1-" + testIndex) {
            @Override
            public void setup() {
                Behaviour.sequence(
                        Behaviour.of(() -> xs.add(1)),
                        Behaviour.of(() -> xs.add(2)),
                        Behaviour.of(() -> xs.add(3))
                ).andThen(AgentFSM::stop)
                .addTo(this);
            }
        }).start();

        mas.awaitAllAgents(MAX_WAIT);

        Assert.assertEquals(Arrays.asList(1, 2, 3), xs);
    }

    @Test
    public void testSequence2() throws Exception {
        final List<Integer> xs = new LinkedList<>();

        mas.registerAgent(new AbstractAgent("testSequence2-" + testIndex) {
            @Override
            public void setup() {
                Behaviour.of(() -> xs.add(1))
                        .andThen(() -> xs.add(2))
                        .andThen(() -> xs.add(3))
                        .andThen(AgentFSM::stop)
                        .addTo(this);
            }
        }).start();

        mas.awaitAllAgents(MAX_WAIT);

        Assert.assertEquals(Arrays.asList(1, 2, 3), xs);
    }

    @Test
    public void testJoin() throws Exception {
        final List<Object> xs = new LinkedList<>();

        mas.registerAgent(new AbstractAgent("testJoin-" + testIndex) {
            @Override
            public void setup() {
                Behaviour.allOf(
                        Behaviour.of(() -> xs.add(1))
                                .andThen(() -> xs.add(2))
                                .andThen(() -> xs.add(3)),

                        Behaviour.of(() -> xs.add("a"))
                                .andThen(() -> xs.add("b"))
                                .andThen(() -> xs.add("c"))
                                .andThen(() -> xs.add("d"))
                ).andThen(AgentFSM::stop)
                .addTo(this);
            }
        }).start();

        mas.awaitAllAgents(MAX_WAIT);

        Assert.assertEquals(Arrays.asList(1, "a", 2, "b", 3, "c", "d"), xs);
    }

    @Test
    public void testParallel() throws Exception {
        final List<Object> xs = new LinkedList<>();

        mas.registerAgent(new AbstractAgent("testParallel-" + testIndex) {
            @Override
            public void setup() {
                Behaviour.anyOf(
                        Behaviour.of(() -> xs.add(1)).
                                andThen(() -> xs.add(2)),

                        Behaviour.of(() -> xs.add("a"))
                                .andThen(() -> xs.add("b"))
                                .andThen(() -> xs.add("c"))
                                .andThen(() -> xs.add("d"))
                ).andThen(AgentFSM::stop)
                .addTo(this);
            }
        }).start();

        mas.awaitAllAgents(MAX_WAIT);

        Assert.assertEquals(Arrays.asList(1, "a", 2), xs);
    }

    @Test
    public void testDoWhile() throws Exception {
        final List<Object> xs = new LinkedList<>();

        mas.registerAgent(new AbstractAgent("testDoWhile-" + testIndex) {
            @Override
            public void setup() {
                Behaviour.of(() -> xs.add(1))
                        .andThen(() -> xs.add(2))
                        .andThen(() -> xs.add(3))
                        .repeatWhile(() -> xs.size() < 7)
                        .andThen(AgentFSM::stop)
                        .addTo(this);
            }
        }).start();

        mas.awaitAllAgents(Duration.ofMillis(Long.MAX_VALUE));

        Assert.assertEquals(Arrays.asList(1, 2, 3, 1, 2, 3, 1, 2, 3), xs);
    }

    @Test
    public void testWait() throws Exception {
        final Duration toWait = Duration.ofSeconds(1);

        final OffsetDateTime start = OffsetDateTime.now();

        mas.registerAgent(new AbstractAgent("testWait-" + testIndex) {

            @Override
            public void setup() {
                Behaviour.waitFor(toWait)
                        .andThen(AgentFSM::stop)
                        .addTo(this);
            }
        }).start();

        mas.awaitAllAgents(Duration.ofMillis(Long.MAX_VALUE));

        Assert.assertTrue(ChronoUnit.MILLIS.between(start, OffsetDateTime.now()) >= toWait.toMillis());
    }

    @Test
    public void testDoForAWhile() throws Exception {
        final Duration toWait = Duration.ofSeconds(1);
        final List<Integer> xs = new LinkedList<>();
        final OffsetDateTime start = OffsetDateTime.now();
        final AtomicInteger i = new AtomicInteger(0);

        mas.registerAgent(new AbstractAgent("testDoForAWhile-" + testIndex) {

            @Override
            public void setup() {
                Behaviour.anyOf(
                        Behaviour.waitFor(toWait),
                        Behaviour.of(() -> xs.add(i.getAndIncrement())).repeatForEver()
                ).andThen(AgentFSM::stop)
                .addTo(this);
            }
        }).start();

        mas.awaitAllAgents(Duration.ofMillis(Long.MAX_VALUE));

        Assert.assertTrue(ChronoUnit.MILLIS.between(start, OffsetDateTime.now()) >= toWait.toMillis());
        Assert.assertTrue(i.get() > 0);
        Assert.assertEquals(IntStream.range(0, i.get()).boxed().collect(Collectors.toList()), xs);
    }
}

