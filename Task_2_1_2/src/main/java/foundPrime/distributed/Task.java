package foundPrime.distributed;

import java.io.Serializable;

public class Task implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int taskId;
    private final long[] numbers;

    public Task(int taskId, long[] numbers) {
        this.taskId = taskId;
        this.numbers = numbers;
    }

    public int getTaskId() {
        return taskId;
    }

    public long[] getNumbers() {
        return numbers;
    }
}
