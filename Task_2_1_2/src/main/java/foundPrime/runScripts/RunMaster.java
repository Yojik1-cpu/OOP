package foundPrime.runScripts;

import foundPrime.network.Master;
import foundPrime.distributed.DistributedPrimeFinder;

import java.util.Arrays;

public class RunMaster {
    public static void main(String[] args) {
        int size = 100_000;
        long[] primes = new long[size];
        Arrays.fill(primes, 1000000007L);

        int expectedWorkers = args.length > 0 ? Integer.parseInt(args[0]) : 1;
        int port = 8080;
        if (args.length > 1) {
            port = Integer.parseInt(args[1]);
        }

        System.out.println("Starting Master node. Awaiting connections from " + expectedWorkers + " Worker(s)...");

        try (Master master = new Master(port, 300_000)) {
            DistributedPrimeFinder finder = new DistributedPrimeFinder(master, expectedWorkers);
            
            long start = System.currentTimeMillis();
            boolean hasNonPrime = finder.hasNonPrime(primes);
            long end = System.currentTimeMillis();
            
            System.out.println("Calculations completed!");
            System.out.println("Has non-prime number: " + hasNonPrime);
            System.out.println("Time taken: " + (end - start) + " ms");
        }
        
        System.exit(0);
    }
}