package foundPrime.distributed;

import foundPrime.network.Master;

import java.util.ArrayList;
import java.util.List;

public class DistributedPrimeFinder {
    private final Master master;
    private final int numWorkersExpected;

    public DistributedPrimeFinder(Master master, int numWorkersExpected) {
        this.master = master;
        this.numWorkersExpected = numWorkersExpected;
    }

    public boolean hasNonPrime(long[] numbers) {
        int chunkSize = Math.max(1, numbers.length / (numWorkersExpected * 4));
        int totalTasks = (int) Math.ceil((double) numbers.length / chunkSize);

        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < totalTasks; i++) {
            int start = i * chunkSize;
            int length = Math.min(chunkSize, numbers.length - start);
            long[] chunk = new long[length];
            System.arraycopy(numbers, start, chunk, 0, length);
            tasks.add(new Task(i, chunk));
        }

        return master.processTasks(tasks);
    }
}