package foundPrime.distributed;

import java.io.Serializable;

public class Result implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int taskId;
    private final boolean hasNonPrime;
    private final String workerId;

    public Result(int taskId, boolean hasNonPrime, String workerId) {
        this.taskId = taskId;
        this.hasNonPrime = hasNonPrime;
        this.workerId = workerId;
    }

    public int getTaskId() {
        return taskId;
    }

    public boolean hasNonPrime() {
        return hasNonPrime;
    }
    
    public String getWorkerId() {
        return workerId;
    }
}
