import PizzeriaCore.SharedQueue;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SharedQueueTest {

    @Test
    void testAddAndTake() throws InterruptedException {
        SharedQueue<String> queue = new SharedQueue<>(1);
        queue.add("Test");
        assertEquals("Test", queue.take());
    }

    @Test
    void testCapacityLimit() throws InterruptedException {
        SharedQueue<String> queue = new SharedQueue<>(1);
        queue.add("First");
        
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(100);
                queue.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        thread.start();

        long start = System.currentTimeMillis();
        queue.add("Second");
        long end = System.currentTimeMillis();
        
        assertTrue((end - start) >= 100);
    }

    @Test
    void testStop() throws InterruptedException {
        SharedQueue<String> queue = new SharedQueue<>(1);
        queue.stop();
        
        Thread thread = new Thread(() -> {
            try {
                queue.take();
            } catch (InterruptedException e) {
            }
        });
        thread.start();
        thread.join(1000);
        
        assertFalse(thread.isAlive());
    }

    @Test
    void testDrainTo() throws InterruptedException {
        SharedQueue<String> queue = new SharedQueue<>(3);
        queue.add("One");
        queue.add("Two");
        queue.add("Three");
        
        List<String> list = new ArrayList<>();
        queue.drainTo(list);
        
        assertEquals(3, list.size());
        assertTrue(list.contains("One"));
        assertTrue(list.contains("Two"));
        assertTrue(list.contains("Three"));
        assertEquals(0, queue.size());
    }
}
