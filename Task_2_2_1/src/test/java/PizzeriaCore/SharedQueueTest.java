package PizzeriaCore;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

public class SharedQueueTest {

    @Test
    void testAddAndTake() throws InterruptedException {
        SharedQueue<Integer> queue = new SharedQueue<>(5);
        queue.add(1);
        queue.add(2);

        assertEquals(2, queue.size());
        assertEquals(1, queue.take());
        assertEquals(2, queue.take());
        assertEquals(0, queue.size());
    }

    @Test
    void testAddBlocksWhenFull() throws InterruptedException {
        SharedQueue<Integer> queue = new SharedQueue<>(1);
        queue.add(1);
        CountDownLatch latch = new CountDownLatch(1);

        Thread producer = new Thread(() -> {
            try {
                queue.add(2); // Should block here
                latch.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        producer.start();

        Thread.sleep(100);
        assertEquals(1, latch.getCount(), "Producer should be blocked");

        queue.take(); // Unblock
        assertTrue(latch.await(1, TimeUnit.SECONDS), "Producer should have been unblocked");
        producer.join();
    }

    @Test
    void testTakeBlocksWhenEmpty() throws InterruptedException {
        SharedQueue<Integer> queue = new SharedQueue<>(1);
        CountDownLatch latch = new CountDownLatch(1);

        Thread consumer = new Thread(() -> {
            try {
                queue.take(); // Should block here
                latch.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        consumer.start();

        Thread.sleep(100);
        assertEquals(1, latch.getCount(), "Consumer should be blocked");

        queue.add(1); // Unblock
        assertTrue(latch.await(1, TimeUnit.SECONDS), "Consumer should have been unblocked");
        consumer.join();
    }

    @Test
    void testStopUnblocksThreads() throws InterruptedException {
        SharedQueue<Integer> queue = new SharedQueue<>(1);
        CountDownLatch latch = new CountDownLatch(2);

        Thread consumer = new Thread(() -> {
            try {
                queue.take(); // Will block
                latch.countDown();
            } catch (InterruptedException e) {
                // This is expected
            }
        });

        queue.add(1);
        Thread producer = new Thread(() -> {
            try {
                queue.add(2); // Will block
                latch.countDown();
            } catch (InterruptedException e) {
                // This is expected
            }
        });
        consumer.start();
        producer.start();

        Thread.sleep(100);
        queue.stop();

        assertTrue(latch.await(1, TimeUnit.SECONDS), "Both threads should have been unblocked by stop()");
        assertNull(queue.take()); // take() on stopped empty queue returns null
        consumer.join();
        producer.join();
    }

    @Test
    void testPollOnEmptyQueue() {
        SharedQueue<Integer> queue = new SharedQueue<>(1);
        assertNull(queue.poll());
    }

    @Test
    void testDrainTo() throws InterruptedException {
        SharedQueue<Integer> queue = new SharedQueue<>(5);
        queue.add(1);
        queue.add(2);
        queue.add(3);

        List<Integer> list = new ArrayList<>();
        queue.drainTo(list);

        assertEquals(3, list.size());
        assertEquals(0, queue.size());
        assertTrue(list.contains(1));
        assertTrue(list.contains(2));
        assertTrue(list.contains(3));
    }
}
