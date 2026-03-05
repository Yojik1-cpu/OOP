import foundPrime.ParallelStreamPrimeFinder;
import foundPrime.SequentialPrimeFinder;
import foundPrime.ThreadPrimeFinder;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class Main {
    private static final int ARRAY_SIZE = 100000;
    private static final long LARGE_PRIME = Integer.MAX_VALUE;

    public static void main(String[] args) throws IOException {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
        long[] numbers = generateTestArray();

        System.out.println("Массив: длина = " + numbers.length + ", все элементы — простое " + LARGE_PRIME);

        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("Доступно потоков: " + cores);
        System.out.println();

        // прогрев JIT
        new SequentialPrimeFinder().hasNonPrime(numbers);
        new ThreadPrimeFinder(2).hasNonPrime(numbers);
        new ParallelStreamPrimeFinder().hasNonPrime(numbers);

        long sequentialMs = measure(() -> new SequentialPrimeFinder().hasNonPrime(numbers));
        System.out.println("Последовательный поиск (SequentialPrimeFinder):     " + sequentialMs + " мс");

        int[] threadCounts = {2, 5, 10, cores};
        for (int threads : threadCounts) {
            long ms = measure(() -> new ThreadPrimeFinder(threads).hasNonPrime(numbers));
            double speedup = sequentialMs > 0 ? (double) sequentialMs / ms : 0;
            System.out.printf("Параллельный Thread (%d потоков):                  %d мс  (ускорение %.2fx)%n",
                    threads, ms, speedup);
        }

        long streamMs = measure(() -> new ParallelStreamPrimeFinder().hasNonPrime(numbers));
        double streamSpeedup = sequentialMs > 0 ? (double) sequentialMs / streamMs : 0;
        System.out.printf("Параллельный parallelStream():                      %d мс  (ускорение %.2fx)%n",
                streamMs, streamSpeedup);

        System.out.println();
        System.out.println("Результат:" + new SequentialPrimeFinder().hasNonPrime(numbers));
    }

    private static long[] generateTestArray() {
        long[] a = new long[ARRAY_SIZE];
        for (int i = 0; i < a.length; i++) {
            a[i] = LARGE_PRIME;
        }
        return a;
    }

    private static long measure(Runnable task) {
        long start = System.nanoTime();
        task.run();
        return (System.nanoTime() - start) / 1_000_000;
    }
}
