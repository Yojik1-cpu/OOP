package PizzeriaCore;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

public class SharedQueue<T> {
    private final Queue<T> queue = new LinkedList<>();
    private final int capacity;
    private boolean running = true;

    public SharedQueue(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void add(T item) throws InterruptedException {
        while (queue.size() == capacity && running) {
            wait();
        }
        if (!running) return;
        queue.add(item);
        notifyAll();
    }

    public synchronized T take() throws InterruptedException {
        while (queue.isEmpty() && running) {
            wait();
        }
        if (!running && queue.isEmpty()) return null;
        T item = queue.poll();
        notifyAll();
        return item;
    }

    public synchronized T poll() {
        if (queue.isEmpty()) {
            return null;
        }
        T item = queue.poll();
        notifyAll();
        return item;
    }

    public synchronized int size() {
        return queue.size();
    }

    public synchronized void stop() {
        running = false;
        notifyAll();
    }

    public synchronized void drainTo(Collection<T> collection) {
        collection.addAll(queue);
        queue.clear();
        notifyAll();
    }
}
