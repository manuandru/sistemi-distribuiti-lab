package sd.lab.agency.behaviour;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import sd.lab.agency.Environment;
import sd.lab.agency.fsm.impl.ThreadBasedAgentFSM;
import sd.lab.agency.impl.AbstractAgent;
import sd.lab.test.ConcurrentTestHelper;

import java.time.Duration;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static sd.lab.agency.behaviour.Behaviour.*;

@RunWith(Parameterized.class)
public class TestSendAndReceive {

    private static final int TEST_REPETITIONS = 5;
    private static final Duration MAX_WAIT = Duration.ofSeconds(3);

    public TestSendAndReceive(Integer i) {
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
    public void testPingPong() throws Exception {
        test.setThreadCount(2);

        var pinger = environment.registerAgent(new AbstractAgent("pinger") {
            @Override
            public void setup() {
                addBehaviour(
                        sequence(
                                send(environment.aidOf("ponger"), "ping"),
                                receiveAnyMessageFromAnyone((sender, message) -> {
                                    test.assertEquals(sender.getLocalName(), "ponger");
                                    test.assertEquals(message, "pong");
                                }),
                                stopAgent()
                        )
                );
            }

            @Override
            public void tearDown() {
                test.done();
            }
        });

        var ponger = environment.registerAgent(new AbstractAgent("ponger") {
            @Override
            public void setup() {
                addBehaviour(
                        sequence(
                                receiveAnyMessageFromAnyone((sender, message) -> {
                                    test.assertEquals(sender.getLocalName(), "pinger");
                                    test.assertEquals(message, "ping");
                                }),
                                send(environment.aidOf("pinger"), "pong"),
                                stopAgent()
                        )
                );
            }

            @Override
            public void tearDown() {
                test.done();
            }
        });

        pinger.start();
        ponger.start();
        test.await();
        pinger.await();
        ponger.await();
    }

    @Test
    public void testMultiplePingPong() throws Exception {
        test.setThreadCount(2);

        var pinger = environment.registerAgent(new AbstractAgent("pinger") {

            private int i = 0;

            @Override
            public void setup() {
                addBehaviour(
                        sequence(
                                send(environment.aidOf("ponger"), "ping" + i++),
                                receiveAnyMessageFromAnyone((sender, message) -> {
                                    test.assertEquals(sender.getLocalName(), "ponger");
                                    test.assertEquals(message, "pong" + (i - 1), "Wrong pong: expected pong" + (i - 1) + ", got " + message);
                                })
                        ).repeatForEver()
                );
                addBehaviour(
                        receiveFromAnyone("stop", (sender, message) -> {
                            test.assertEquals(sender.getLocalName(), "ponger");
                            test.assertEquals(message, "stop");
                        }).andThen(stopAgent())
                );
            }

            @Override
            public void tearDown() {
                test.done();
            }
        });

        var ponger = environment.registerAgent(new AbstractAgent("ponger") {
            @Override
            public void setup() {
                sequence(
                        receiveAnyMessageFromAnyone((sender, message) -> {
                            test.assertEquals(sender.getLocalName(), "pinger");
                            test.assertTrue(message.startsWith("ping"));
                            addBehaviour(
                                    send(environment.aidOf("pinger"), message.replace("ping", "pong"))
                            );
                        }).repeatManyTimes(10),
                        send(environment.aidOf("pinger"), "stop"),
                        stopAgent()
                ).addTo(this);
            }

            @Override
            public void tearDown() {
                test.done();
            }
        });

        pinger.start();
        ponger.start();
        test.await();
        pinger.await();
        ponger.await();
    }
}
