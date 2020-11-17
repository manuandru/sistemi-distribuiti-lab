package sd.lab.linda.textual;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RunWith(Parameterized.class)
public class TestTextualSpace extends AbstractTestTextualSpace {

    public TestTextualSpace(Integer i) {
        super(i);
    }

    @Parameterized.Parameters
    public static Iterable<Integer> data() {
        return IntStream.range(0, TEST_REPETITIONS).boxed().collect(Collectors.toList());
    }

    @Override
    protected TextualSpace newTupleSpace(String testName) {
        return TextualSpace.of("ts" + testName + "-" + testIndex, executor);
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
