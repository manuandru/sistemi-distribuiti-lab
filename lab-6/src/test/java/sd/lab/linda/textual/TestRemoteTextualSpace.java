package sd.lab.linda.textual;

import io.javalin.Javalin;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import sd.lab.ws.Service;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RunWith(Parameterized.class)
public class TestRemoteTextualSpace extends AbstractTestTextualSpace {

    private static final int TEST_PORT = 8082;
    private static Javalin server;

    public TestRemoteTextualSpace(Integer i) {
        super(i);
    }

    @Parameterized.Parameters
    public static Iterable<Integer> data() {
        return IntStream.range(0, TEST_REPETITIONS).boxed().collect(Collectors.toList());
    }

    @Override
    protected TextualSpace newTupleSpace(String testName) {
        return TextualSpace.remote("localhost", TEST_PORT, "ts-" + testName + "-" + testIndex);
    }

    @BeforeClass
    public static void setUpSuite() throws Exception {
        server = Service.startService(TEST_PORT);
    }

    @AfterClass
    public static void tearDownSuite() throws Exception {
        server.stop();
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @After
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }
}
