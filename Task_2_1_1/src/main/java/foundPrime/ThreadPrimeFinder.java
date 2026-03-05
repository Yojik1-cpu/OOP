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
                    if (!PrimeFinder.isPrime(numbers[i])) {
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

    private static class MyAtomicInteger {
        private int value;

        public MyAtomicInteger(int initialValue) {
            this.value = initialValue;
        }

        public synchronized int get() {
            return value;
        }

        public synchronized void set(int newValue) {
            this.value = newValue;
        }

        public synchronized boolean compareAndSet(int expectedValue, int newValue) {
            if (this.value == expectedValue) {
                this.value = newValue;
                return true;
            }
            return false;
        }
    }

    public int getThreadCount() {
        return threadCount;
    }
}
