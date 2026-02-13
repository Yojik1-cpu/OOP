package foundPrime;

import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadPrimeFinder implements PrimeFinder {

    private final int threadCount;

    public ThreadPrimeFinder(int threadCount) {
        if (threadCount < 1) {
            throw new IllegalArgumentException("Количество потоков должно быть не менее 1. Получено: " + threadCount);
        }
        this.threadCount = threadCount;
    }

    @Override
    public boolean hasNonPrime(long[] numbers) {
        if (numbers == null || numbers.length == 0) {
            return false;
        }
        int n = numbers.length;
        int threadsToUse = Math.min(threadCount, n);
        AtomicBoolean foundNonPrime = new AtomicBoolean(false);

        Thread[] workers = new Thread[threadsToUse];
        for (int t = 0; t < threadsToUse; t++) {
            final int threadIndex = t;
            workers[t] = new Thread(() -> {
                for (int i = threadIndex; i < n && !foundNonPrime.get(); i += threadsToUse) {
                    if (!SequentialPrimeFinder.isPrime(numbers[i])) {
                        foundNonPrime.set(true);
                        return;
                    }
                }
            });
            workers[t].start();
        }

        try {
            for (Thread w : workers) {
                w.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException();
        }

        return foundNonPrime.get();
    }

    public int getThreadCount() {
        return threadCount;
    }
}
