package foundPrime.distributed;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskResultTest {

    @Test
    void testTaskCreationAndGetters() {
        long[] numbers = {1, 2, 3};
        Task task = new Task(10, numbers);
        
        assertEquals(10, task.getTaskId());
        assertArrayEquals(numbers, task.getNumbers());
    }

    @Test
    void testResultCreationAndGetters() {
        Result result = new Result(5, true, "worker-1");
        
        assertEquals(5, result.getTaskId());
        assertTrue(result.hasNonPrime());
        assertEquals("worker-1", result.getWorkerId());
    }
}
