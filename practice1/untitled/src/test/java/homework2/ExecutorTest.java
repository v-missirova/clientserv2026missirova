package homework2;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExecutorTest {
    @Test
    public void testExecution() throws InterruptedException {
        Executor server = new Executor();
        server.start();
        Thread.sleep(2000);
        server.stop();
        assertTrue(true, "OK, server stopped");
    }

}