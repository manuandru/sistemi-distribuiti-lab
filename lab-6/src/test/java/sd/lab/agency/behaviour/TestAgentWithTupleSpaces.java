package sd.lab.agency.behaviour;

import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.multiset.HashMultiSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import sd.lab.agency.Environment;
import sd.lab.agency.fsm.impl.ThreadBasedAgentFSM;
import sd.lab.agency.impl.AbstractAgent;
import sd.lab.linda.textual.RegexTemplate;
import sd.lab.linda.textual.StringTuple;
import sd.lab.test.ConcurrentTestHelper;

import java.time.Duration;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static sd.lab.agency.behaviour.Behaviour.*;

@RunWith(Parameterized.class)
public class TestAgentWithTupleSpaces {

    private static final int TEST_REPETITIONS = 5;
    private static final Duration MAX_WAIT = Duration.ofSeconds(3);

    public TestAgentWithTupleSpaces(Integer i) {
        testIndex = i;
    }

    private final int testIndex;
    private ConcurrentTestHelper test;
    private Random rand;
    private Environment<ThreadBasedAgentFSM> environment;

    @Parameterized.Parameters
    public static Iterable<Integer> data() {
        return IntStream.range(0, TEST_REPETITIONS).boxed().collect(Collectors.toList());
    }

    @Before
    public void setUp() {
//        executor = Executors.newSingleThreadExecutor();
        test = new ConcurrentTestHelper();
        rand = new Random();
        environment = Environment.multiThreaded("env-" + testIndex);
    }

    @Test
    public void testInitiallyEmpty() throws Exception {
        test.setThreadCount(1);

        var tsName = "ts-testInitiallyEmpty-" + testIndex;

        var alice = environment.registerAgent(new AbstractAgent("Alice-testInitiallyEmpty-" + testIndex) {
            @Override
            public void setup() {
                addBehaviour(
                        allOf(
                                count(tsName, size -> test.assertEquals(0, size)),
                                get(tsName, tuples -> test.assertEquals(new HashMultiSet<>(), tuples))
                        ).andThen(stopAgent())
                );
            }

            @Override
            public void tearDown() {
                test.done();
            }
        });

        alice.start();

        test.await();
        alice.await();
    }


    @Test
    public void testReadSuspensiveSemantics() throws Exception {
        test.setThreadCount(1);

        var tsName = "ts-testReadSuspensiveSemantics-" + testIndex;

        var alice = environment.registerAgent(new AbstractAgent("Alice-testReadSuspensiveSemantics-" + testIndex) {
            @Override
            public void setup() {
                addBehaviour(
                        anyOf(
                                rd(tsName, RegexTemplate.of(".*"), tuple -> test.fail()),
                                waitFor(MAX_WAIT)
                        ).andThen(stopAgent())
                );
            }

            @Override
            public void tearDown() {
                test.done();
            }

        });

        alice.start();

        test.await();
        alice.await();
    }

    @Test
    public void testTakeSuspensiveSemantics() throws Exception {
        test.setThreadCount(1);

        var tsName = "ts-testTakeSuspensiveSemantics-" + testIndex;

        var alice = environment.registerAgent(new AbstractAgent("Alice-testTakeSuspensiveSemantics-" + testIndex) {

            @Override
            public void setup() {
                addBehaviour(
                        anyOf(
                                in(tsName, RegexTemplate.of(".*"), tuple -> test.fail()),
                                waitFor(MAX_WAIT)
                        ).andThen(stopAgent())
                );
            }

            @Override
            public void tearDown() {
                test.done();
            }

        });

        alice.start();

        test.await();
        alice.await();
    }


    @Test
    public void testWriteGenerativeSemantics() throws Exception {
        test.setThreadCount(1);

        var tsName = "ts-testWriteGenerativeSemantics-" + testIndex;

        var alice = environment.registerAgent(new AbstractAgent("Alice-testWriteGenerativeSemantics-" + testIndex) {

            @Override
            public void setup() {
                addBehaviour(
                        sequence(
                                count(tsName, size -> test.assertEquals(0, size)),
                                out(tsName, StringTuple.of("foo")),
                                count(tsName, size -> test.assertEquals(1, size)),
                                stopAgent()
                        )
                );
            }

            @Override
            public void tearDown() {
                test.done();
            }

        });

        alice.start();

        test.await();
        alice.await();
    }

    @Test
    public void testReadIsIdempotent() throws Exception {
        test.setThreadCount(2);

        var tuple = StringTuple.of("foo bar");
        var tsName = "ts-testReadIsIdempotent-" + testIndex;

        var bob = environment.registerAgent(new AbstractAgent("Bob-testReadIsIdempotent-" + testIndex) {
            @Override
            public void setup() {
                addBehaviour(
                        sequence(
                                rd(tsName, RegexTemplate.of(".*?foo.*"), t -> test.assertEquals(t, tuple))
                                        .repeatManyTimes(rand.nextInt(10) + 1),
                                rd(tsName, RegexTemplate.of(".*?bar.*"), t -> test.assertEquals(t, tuple)),
                                stopAgent()
                        )
                );
            }

            @Override
            public void tearDown() {
                test.done();
            }

        });

        bob.start();

        var alice = environment.registerAgent(new AbstractAgent("Alice-testReadIsIdempotent-" + testIndex) {

            @Override
            public void setup() {
                addBehaviour(
                        out(tsName, tuple).andThen(stopAgent())
                );
            }

            @Override
            public void tearDown() {
                test.done();
            }

        });

        alice.start();

        test.await();
        alice.await();
        bob.await();
    }

    @Test
    public void testTakeIsNotIdempotent2() throws Exception {
        test.setThreadCount(2);

        var tuple = StringTuple.of("foo bar");
        var tsName = "ts-testTakeIsNotIdempotent2-" + testIndex;

        var alice = environment.registerAgent(new AbstractAgent("Alice-testTakeIsNotIdempotent2-" + testIndex) {

            @Override
            public void setup() {
                addBehaviour(
                        out(tsName, tuple).andThen(stopAgent())
                );
            }

            @Override
            public void tearDown() {
                test.done();
            }

        });

        var bob = environment.registerAgent(new AbstractAgent("Bob-testTakeIsNotIdempotent2-" + testIndex) {

            @Override
            public void setup() {
                addBehaviour(
                        sequence(
                                allOf(
                                        in(tsName, RegexTemplate.of(".*?foo.*"), t -> test.assertEquals(t, tuple)),
                                        Behaviour.of(alice::start)
                                ),
                                anyOf(
                                        in(tsName, RegexTemplate.of(".*?bar.*"), t -> test.fail()),
                                        waitFor(MAX_WAIT)
                                ),
                                stopAgent()
                        )
                );
            }

            @Override
            public void tearDown() {
                test.done();
            }

        });

        bob.start();

        test.await();
        alice.await();
        bob.await();
    }

    @Test
    public void testTakeIsNotIdempotent1() throws Exception {
        test.setThreadCount(2);

        var tuple = StringTuple.of("foo bar");
        var tsName = "ts-testTakeIsNotIdempotent1-" + testIndex;

        var bob = environment.registerAgent(new AbstractAgent("Bob-testTakeIsNotIdempotent1-" + testIndex) {

            @Override
            public void setup() {
                addBehaviour(
                        sequence(
                                in(tsName, RegexTemplate.of(".*?foo.*"), t -> test.assertEquals(t, tuple)),
                                anyOf(
                                        in(tsName, RegexTemplate.of(".*?bar.*"), t -> test.fail()),
                                        waitFor(MAX_WAIT)
                                ),
                                stopAgent()
                        )
                );
            }

            @Override
            public void tearDown() {
                test.done();
            }

        });

        var alice = environment.registerAgent(new AbstractAgent("Alice-testTakeIsNotIdempotent1-" + testIndex) {
            @Override
            public void setup() {
                addBehaviour(
                        sequence(
                                out(tsName, tuple),
                                Behaviour.of(bob::start),
                                stopAgent()
                        )
                );
            }

            @Override
            public void tearDown() {
                test.done();
            }

        });

        alice.start();

        test.await();
        alice.await();
        bob.await();
    }

    @Test
    public void testAssociativeAccess() throws Exception {
        test.setThreadCount(3);

        var tuple4Bob = StringTuple.of("recipient: bob");
        var tuple4Carl = StringTuple.of("recipient: carl");

        var tsName = "ts-testAssociativeAccess-" + testIndex;

        var carl = environment.registerAgent(new AbstractAgent("Carl-testAssociativeAccess-" + testIndex) {
            @Override
            public void setup() {
                addBehaviour(
                        sequence(
                                rd(tsName, RegexTemplate.of(".*?carl.*"), t -> test.assertEquals(t, tuple4Carl)),
                                stopAgent()
                        )
                );
            }

            @Override
            public void tearDown() {
                test.done();
            }
        });

        carl.start();

        var bob = environment.registerAgent(new AbstractAgent("Bob-testAssociativeAccess-" + testIndex) {
            @Override
            public void setup() {
                addBehaviour(
                        sequence(
                                rd(tsName, RegexTemplate.of(".*?bob.*"), t -> test.assertEquals(t, tuple4Bob)),
                                stopAgent()
                        )
                );
            }

            @Override
            public void tearDown() {
                test.done();
            }
        });

        bob.start();

        var alice = environment.registerAgent(new AbstractAgent("Alice-testAssociativeAccess-" + testIndex) {
            @Override
            public void setup() {
                addBehaviour(
                        sequence(
                                out(tsName, tuple4Bob),
                                out(tsName, tuple4Carl),
                                in(tsName, RegexTemplate.of("recipient:.*"), t -> test.assertOneOf(t, tuple4Bob, tuple4Carl)),
                                in(tsName, RegexTemplate.of("recipient:.*"), t -> test.assertOneOf(t, tuple4Bob, tuple4Carl)),
                                stopAgent()
                        )
                );
            }

            @Override
            public void tearDown() {
                test.done();
            }

        });

        alice.start();

        test.await();
        alice.await();
        bob.await();
        carl.await();
    }

    @Test
    public void testCount() throws Exception {
        test.setThreadCount(1);

        var tuple = StringTuple.of("a");
        var tsName = "ts-testCount-" + testIndex;

        var alice = environment.registerAgent(new AbstractAgent("Alice-testCount-" + testIndex) {
            @Override
            public void setup() {
                addBehaviour(
                        sequence(
                                count(tsName, size -> test.assertEquals(size, 0)),
                                out(tsName, tuple),
                                count(tsName, size -> test.assertEquals(size, 1)),
                                out(tsName, tuple),
                                count(tsName, size -> test.assertEquals(size, 2)),
                                out(tsName, tuple),
                                count(tsName, size -> test.assertEquals(size, 3)),
                                in(tsName, RegexTemplate.of("a"), t -> test.assertEquals(t, tuple)),
                                count(tsName, size -> test.assertEquals(size, 2)),
                                stopAgent()
                        )
                );
            }

            @Override
            public void tearDown() {
                test.done();
            }
        });

        alice.start();

        test.await();
        alice.await();
    }

    @Test
    public void testGetAll() throws Exception {
        test.setThreadCount(1);

        final MultiSet<StringTuple> expected = new HashMultiSet<>(Arrays.asList(
                StringTuple.of("b"),
                StringTuple.of("c"),
                StringTuple.of("a"),
                StringTuple.of("b")
        ));
        var tsName = "ts-testGetAll-" + testIndex;

        var alice = environment.registerAgent(new AbstractAgent("Alice-testGetAll-" + testIndex) {

            @Override
            public void setup() {
                addBehaviour(
                        sequence(
                                out(tsName, StringTuple.of("a")),
                                out(tsName, StringTuple.of("b")),
                                out(tsName, StringTuple.of("b")),
                                out(tsName, StringTuple.of("c")),
                                get(tsName, tuples -> test.assertEquals(tuples, expected)),
                                stopAgent()
                        )
                );
            }

            @Override
            public void tearDown() {
                test.done();
            }
        });

        alice.start();

        test.await();
        alice.await();
    }
}
