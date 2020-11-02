package sd.lab.agency;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import sd.lab.agency.fsm.impl.ExecutorBasedAgentFSM;
import sd.lab.agency.fsm.impl.ThreadBasedAgentFSM;
import sd.lab.test.ConcurrentTestHelper;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RunWith(Parameterized.class)
public class TestThreadedAgentFSM {

    private static final Duration MAX_WAIT = Duration.ofSeconds(2);
    private static final int TEST_REPETITIONS = 5;

    protected Random rand;
    protected Environment<ThreadBasedAgentFSM> mas;

    private final int testIndex;

    @Parameterized.Parameters
    public static Iterable<Integer> data() {
        return IntStream.range(0, TEST_REPETITIONS).boxed().collect(Collectors.toList());
    }

    public TestThreadedAgentFSM(Integer i) {
        testIndex = i;
    }

    @Before
    public void setUp() {
        rand = new Random();
        mas = Environment.multiThreaded(TestThreadedAgentFSM.class.getSimpleName() + "-Environment-" + testIndex);
    }

    @After
    public void tearDown() throws InterruptedException, ExecutionException, TimeoutException {
        mas.awaitAllAgents(MAX_WAIT);
    }

    // TODO readme
    @Test
    public void testAgentsFlow() throws Exception {
        final List<Integer> observableEvents = new LinkedList<>();

        mas.registerAgent(new ThreadBasedAgentFSM("Alice") {
            int x = 0;

            @Override
            public void onBegin() throws Exception {
                observableEvents.add(-1);
                throw new RuntimeException("Ignore me");
            }

            @Override
            public void onRun() throws Exception {
                if (x < 10) {
                    observableEvents.add(x++);
                } else {
                    throw new Exception("Stop the Agent now!");
                }
            }

            @Override
            public void onUncaughtError(Exception e) {
                if (e instanceof RuntimeException) {
                    observableEvents.add(-1);
                } else {
                    observableEvents.add(x++);
                    stop();
                }
            }

            @Override
            public void onEnd() {
                observableEvents.add(x);
            }

        }).start();

        mas.awaitAllAgents(MAX_WAIT);

        Assert.assertEquals(
                List.of(-1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
                observableEvents
        );
    }

    // TODO readme
    @Test
    public void testAgentsRestart() throws Exception {
        final List<Integer> observableEvents = new LinkedList<>();

        mas.registerAgent(new ThreadBasedAgentFSM("Alice") {
            int x = -1;

            @Override
            public void onBegin() throws Exception {
                observableEvents.add(x);
                x += 2;
                if (x == 1) {
                    throw new RuntimeException("Restart the agent!");
                }
            }

            @Override
            public void onRun() throws Exception {
                if (x == 10) {
                    observableEvents.add(x++);
                    throw new RuntimeException("Restart the agent!");
                } else if (x == 15) {
                    observableEvents.add(x++);
                    throw new Exception("Stop the agent!");
                } else {
                    observableEvents.add(x++);
                }
            }

            @Override
            public void onUncaughtError(Exception e) {
                if (e instanceof RuntimeException) {
                    observableEvents.add(-1);
                    restart();
                } else {
                    observableEvents.add(x++);
                    stop();
                }
            }

            @Override
            public void onEnd() throws Exception {
                observableEvents.add(x);
            }

        }).start();


        mas.awaitAllAgents(MAX_WAIT);

        Assert.assertEquals(
                List.of(-1, -1, 1, 3, 4, 5, 6, 7, 8, 9, 10, -1, 11, 13, 14, 15, 16, 17),
                observableEvents
        );
    }

    // TODO readme
    @Test
    public void testAgentsRunOnDifferentThreads() throws Exception {
        final List<String> observableEvents = new LinkedList<>();

        final Semaphore mutex1 = new Semaphore(0);
        final Semaphore mutex2 = new Semaphore(0);

        mas.registerAgent(new ThreadBasedAgentFSM("Alice") {

            @Override
            public void onRun() throws Exception {
                mutex1.acquire();
                observableEvents.add("a1");
                mutex2.release();
                stop();
            }
        }).start();

        mas.registerAgent(new ThreadBasedAgentFSM("Bob") {
            @Override
            public void onRun() throws Exception {
                observableEvents.add("b1");
                mutex1.release();
                mutex2.acquire();
                observableEvents.add("b2");
                stop();
            }
        }).start();

        mas.awaitAllAgents(MAX_WAIT);
        Assert.assertEquals(
                List.of("b1", "a1", "b2"),
                observableEvents
        );
    }

    // TODO readme
    @Test
    public void testAgentsPause() throws Exception {
        final List<String> observableEvents = new LinkedList<>();

        final Semaphore mutex = new Semaphore(0);

        var bob = mas.registerAgent(new ThreadBasedAgentFSM("Bob") {

            private boolean first = true;

            @Override
            public void onRun() throws Exception {
                if (first) {
                    first = false;
                    observableEvents.add("b1");
                    pause();
                    observableEvents.add("b2");
                    mutex.release();
                } else {
                    observableEvents.add("b3");
                    stop();
                }
            }
        });

        bob.start();

        mas.registerAgent(new ThreadBasedAgentFSM("Alice") {
            @Override
            public void onRun() throws Exception {
                mutex.acquire();
                Thread.sleep(MAX_WAIT.toMillis() / 2);
                bob.resume();
                stop();
            }
        }).start();

        mas.awaitAllAgents(MAX_WAIT);

        Assert.assertEquals(
                Arrays.asList("b1", "b2", "b3"),
                observableEvents
        );
    }
}
