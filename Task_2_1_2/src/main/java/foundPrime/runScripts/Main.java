package foundPrime.runScripts;

import foundPrime.network.Master;
import foundPrime.distributed.DistributedPrimeFinder;
import foundPrime.network.Worker;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int size = 100_000;
        long[] primes = new long[size];
        for (int i = 0; i < size; i++) {
            primes[i] = 1000000007L;
        }

        int port = 8080;
        int workers = 2;

        Thread masterThread = new Thread(() -> {
            try (Master master = new Master(port, 60000)) {
                DistributedPrimeFinder finder = new DistributedPrimeFinder(master, workers);
                long start = System.currentTimeMillis();
                boolean hasNonPrime = finder.hasNonPrime(primes);
                long end = System.currentTimeMillis();
                
                System.out.println("Has non-prime: " + hasNonPrime);
                System.out.println("Time taken: " + (end - start) + " ms");
            }
        });
        masterThread.start();

        Thread.sleep(1000);

        for (int i = 0; i < workers; i++) {
            new Thread(() -> {
                Worker worker = new Worker("127.0.0.1", port);
                worker.start();
            }).start();
        }
        
        masterThread.join();
        System.exit(0);
    }
}