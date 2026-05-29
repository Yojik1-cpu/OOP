package foundPrime.distributed;

import foundPrime.network.Master;
import foundPrime.network.Worker;
import org.junit.jupiter.api.Test;
import java.util.concurrent.CountDownLatch;
import static org.junit.jupiter.api.Assertions.*;

class MasterWorkerTest {

    @Test
    void testDistributedPrimeFinder_AllPrimes() throws InterruptedException {
        int port = 8081;
        long[] numbers = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29};
        
        try (Master master = new Master(port, 5000)) {
            DistributedPrimeFinder finder = new DistributedPrimeFinder(master, 1);
            
            CountDownLatch workerStarted = new CountDownLatch(1);
            Thread workerThread = new Thread(() -> {
                workerStarted.countDown();
                Worker worker = new Worker("127.0.0.1", port);
                try {
                    worker.start();
                } catch (Exception ignored) {}
            });
            workerThread.setDaemon(true);
            workerThread.start();
            
            workerStarted.await();
            Thread.sleep(200);
            
            boolean hasNonPrime = finder.hasNonPrime(numbers);
            assertFalse(hasNonPrime, "Should not find any non-primes in the array");
        }
    }

    @Test
    void testDistributedPrimeFinder_WithNonPrime() throws InterruptedException {
        int port = 8082;
        long[] numbers = {2, 3, 5, 7, 11, 13, 4, 19, 23, 29};
        
        try (Master master = new Master(port, 5000)) {
            DistributedPrimeFinder finder = new DistributedPrimeFinder(master, 1);
            
            CountDownLatch workerStarted = new CountDownLatch(1);
            Thread workerThread = new Thread(() -> {
                workerStarted.countDown();
                Worker worker = new Worker("127.0.0.1", port);
                try {
                    worker.start();
                } catch (Exception ignored) {}
            });
            workerThread.setDaemon(true);
            workerThread.start();
            
            workerStarted.await();
            Thread.sleep(200);
            
            boolean hasNonPrime = finder.hasNonPrime(numbers);
            assertTrue(hasNonPrime, "Should find the non-prime number (4)");
        }
    }
    
    @Test
    void testDistributedPrimeFinder_MultipleWorkers() throws InterruptedException {
        int port = 8083;
        long[] numbers = new long[100];
        for(int i = 0; i < 100; i++) {
            numbers[i] = 1000000007L;
        }
        numbers[99] = 10;
        
        int workersCount = 3;
        try (Master master = new Master(port, 5000)) {
            DistributedPrimeFinder finder = new DistributedPrimeFinder(master, workersCount);
            
            for (int i = 0; i < workersCount; i++) {
                Thread workerThread = new Thread(() -> {
                    Worker worker = new Worker("127.0.0.1", port);
                    try {
                        worker.start();
                    } catch (Exception ignored) {}
                });
                workerThread.setDaemon(true);
                workerThread.start();
            }
            
            Thread.sleep(500);
            
            boolean hasNonPrime = finder.hasNonPrime(numbers);
            assertTrue(hasNonPrime, "Should find the non-prime number with multiple workers");
        }
    }
}