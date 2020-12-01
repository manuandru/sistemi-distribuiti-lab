package sd.lab.linda.textual;

import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.multiset.HashMultiSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import sd.lab.agency.Environment;
import sd.lab.agency.fsm.impl.ThreadBasedAgentFSM;
import sd.lab.test.ConcurrentTestHelper;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RunWith(Parameterized.class)
public class TestTextualSpace {

    private static final int TEST_REPETITIONS = 5;

    public TestTextualSpace(Integer i) {
        testIndex = i;
    }

    private final int testIndex;
    private ExecutorService executor;
    private TextualSpace tupleSpace;
    private ConcurrentTestHelper test;
    private Random rand;
    private Environment<ThreadBasedAgentFSM> environment;

    @Parameterized.Parameters
    public static Iterable<Integer> data() {
        return IntStream.range(0, TEST_REPETITIONS).boxed().collect(Collectors.toList());
    }

    @Before
    public void setUp() throws Exception {
        executor = Executors.newSingleThreadExecutor();
        tupleSpace = TextualSpace.of("ts" + testIndex, executor);
        test = new ConcurrentTestHelper();
        rand = new Random();
        environment = Environment.multiThreaded("env-" + testIndex);
    }

    @After
    public void tearDown() throws Exception {
        executor.shutdown();
    }

    @Test
    public void testInitiallyEmpty() throws Exception {
        test.setThreadCount(1);

        var alice = environment.registerAgent(new ThreadBasedAgentFSM("Alice") {

            @Override
            public void onRun() throws Exception {
                test.assertEquals(tupleSpace.count(), 0, "The tuple space must initially be empty");
                stop();
            }

            @Override
            public void onEnd() {
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

        var alice = environment.registerAgent(new ThreadBasedAgentFSM("Alice") {

            @Override
            public void onRun() throws Exception {
                test.assertBlocksIndefinitely(tupleSpace.rd(".*"), "A read operation should block if no tuple matching the requested template is available");
                stop();
            }

            @Override
            public void onEnd() {
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

        var alice = environment.registerAgent(new ThreadBasedAgentFSM("Alice") {

            @Override
            public void onRun() throws Exception {
                test.assertBlocksIndefinitely(tupleSpace.in(".*"), "A take operation should block if no tuple matching the requested template is available");
                stop();
            }

            @Override
            public void onEnd() {
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

        var alice = environment.registerAgent(new ThreadBasedAgentFSM("Alice") {

            @Override
            public void onRun() throws Exception {
                test.assertEquals(tupleSpace.count(), 0, "The tuple space must initially be empty");
                test.assertEquals(tupleSpace.out("foo"), StringTuple.of("foo"), "A write operation eventually return the same tuple it received as argument");
                test.assertEquals(tupleSpace.count(), 1, "After a tuple was written, the tuple space size should increase");
                stop();
            }

            @Override
            public void onEnd() {
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

        final StringTuple tuple = StringTuple.of("foo bar");

        var bob = environment.registerAgent(new ThreadBasedAgentFSM("Bob") {

            @Override
            public void onRun() throws Exception {
                for (int i = rand.nextInt(10) + 1; i >= 0; i--) {
                    test.assertEquals(tupleSpace.rd(".*?foo.*"), tuple);
                }
                test.assertEquals(tupleSpace.rd(".*?bar.*"), tuple);
                stop();
            }

            @Override
            public void onEnd() {
                test.done();
            }

        });

        bob.start();

        var alice = environment.registerAgent(new ThreadBasedAgentFSM("Alice") {

            @Override
            public void onRun() throws Exception {
                test.assertEventuallyReturns(tupleSpace.out(tuple));
                stop();
            }

            @Override
            public void onEnd() {
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

        final StringTuple tuple = StringTuple.of("foo bar");

        var alice = environment.registerAgent(new ThreadBasedAgentFSM("Alice") {

            @Override
            public void onRun() throws Exception {
                test.assertEventuallyReturns(tupleSpace.out(tuple));
                stop();
            }

            @Override
            public void onEnd() {
                test.done();
            }

        });

        var bob = environment.registerAgent(new ThreadBasedAgentFSM("Bob") {

            @Override
            public void onRun() throws Exception {
                final Future<StringTuple> toBeTaken = tupleSpace.in(".*?foo.*");
                alice.start();
                test.assertEquals(toBeTaken, tuple);
                test.assertBlocksIndefinitely(tupleSpace.in(".*?bar.*"));
                stop();
            }

            @Override
            public void onEnd() {
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

        final StringTuple tuple = StringTuple.of("foo bar");

        var bob = environment.registerAgent(new ThreadBasedAgentFSM("Bob") {

            @Override
            public void onRun() throws Exception {
                test.assertEquals(tupleSpace.in(".*?foo.*"), tuple);
                test.assertBlocksIndefinitely(tupleSpace.in(".*?bar.*"));
                stop();
            }

            @Override
            public void onEnd() {
                test.done();
            }

        });

        var alice = environment.registerAgent(new ThreadBasedAgentFSM("Alice") {

            @Override
            public void onRun() throws Exception {
                test.assertEventuallyReturns(tupleSpace.out(tuple));
                bob.start();
                stop();
            }

            @Override
            public void onEnd() {
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

        final StringTuple tuple4Bob = StringTuple.of("recipient: bob");
        final StringTuple tuple4Carl = StringTuple.of("recipient: carl");

        var carl = environment.registerAgent(new ThreadBasedAgentFSM("Carl") {

            @Override
            public void onRun() throws Exception {
                test.assertEquals(tupleSpace.rd(".*?carl.*"), tuple4Carl);
                stop();
            }

            @Override
            public void onEnd() {
                test.done();
            }

        });

        carl.start();

        var bob = environment.registerAgent(new ThreadBasedAgentFSM("Bob") {

            @Override
            public void onRun() throws Exception {
                test.assertEquals(tupleSpace.rd(".*?bob.*"), tuple4Bob);
                stop();
            }

            @Override
            public void onEnd() {
                test.done();
            }

        });

        bob.start();

        var alice = environment.registerAgent(new ThreadBasedAgentFSM("Alice") {

            @Override
            public void onRun() throws Exception {
                test.assertEventuallyReturns(tupleSpace.out(tuple4Bob));
                test.assertEventuallyReturns(tupleSpace.out(tuple4Carl));

                test.assertOneOf(tupleSpace.in("recipient:.*"), tuple4Bob, tuple4Carl);
                test.assertOneOf(tupleSpace.in("recipient:.*"), tuple4Bob, tuple4Carl);

                stop();
            }

            @Override
            public void onEnd() {
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
    public void testGetSize() throws Exception {
        test.setThreadCount(1);

        var alice = environment.registerAgent(new ThreadBasedAgentFSM("Alice") {

            @Override
            public void onRun() throws Exception {
                test.assertEquals(tupleSpace.count(), 0);
                test.assertEventuallyReturns(tupleSpace.out("a"));
                test.assertEquals(tupleSpace.count(), 1);
                test.assertEventuallyReturns(tupleSpace.out("a"));
                test.assertEquals(tupleSpace.count(), 2);
                test.assertEventuallyReturns(tupleSpace.out("a"));
                test.assertEquals(tupleSpace.count(), 3);

                test.assertEventuallyReturns(tupleSpace.in("a"));
                test.assertEquals(tupleSpace.count(), 2);

                stop();
            }

            @Override
            public void onEnd() {
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

        var alice = environment.registerAgent(new ThreadBasedAgentFSM("Alice") {

            @Override
            public void onRun() throws Exception {
                test.assertEventuallyReturns(tupleSpace.out("a"));
                test.assertEventuallyReturns(tupleSpace.out("b"));
                test.assertEventuallyReturns(tupleSpace.out("b"));
                test.assertEventuallyReturns(tupleSpace.out("c"));

                test.assertEquals(tupleSpace.get(), expected);

                stop();
            }

            @Override
            public void onEnd() {
                test.done();
            }

        });

        alice.start();

        test.await();
        alice.await();
    }
}
