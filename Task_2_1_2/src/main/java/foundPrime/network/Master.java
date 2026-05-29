package foundPrime.network;

import foundPrime.distributed.Result;
import foundPrime.distributed.Task;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Master implements AutoCloseable {
    private final int port;
    private final int timeoutMs;

    private final ConcurrentLinkedQueue<Task> taskQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentHashMap<Integer, Task> runningTasks = new ConcurrentHashMap<>();

    private final AtomicBoolean foundNonPrime = new AtomicBoolean(false);
    private final AtomicInteger completedTasks = new AtomicInteger(0);
    private int totalTasks;

    private final ExecutorService serverExecutor = Executors.newSingleThreadExecutor();
    private final ExecutorService workerPool = Executors.newCachedThreadPool();
    private volatile ServerSocket serverSocket;

    public Master(int port, int timeoutMs) {
        this.port = port;
        this.timeoutMs = timeoutMs;
    }

    public boolean processTasks(List<Task> tasks) {
        this.totalTasks = tasks.size();
        this.taskQueue.addAll(tasks);
        this.foundNonPrime.set(false);
        this.completedTasks.set(0);
        this.runningTasks.clear();

        CountDownLatch doneSignal = new CountDownLatch(1);

        serverExecutor.submit(() -> {
            try (ServerSocket ss = new ServerSocket(port)) {
                this.serverSocket = ss;
                System.out.println("Master started on port " + port);
                ss.setSoTimeout(1000);
                while (!foundNonPrime.get() && completedTasks.get() < totalTasks && !Thread.currentThread().isInterrupted()) {
                    try {
                        Socket workerSocket = ss.accept();
                        System.out.println("Accepted worker from " + workerSocket.getInetAddress());
                        workerPool.submit(new WorkerHandler(workerSocket, doneSignal));
                    } catch (java.net.SocketTimeoutException e) {}
                }
            } catch (IOException e) {
                if (serverSocket == null || !serverSocket.isClosed()) {
                    System.err.println("Master server socket error: " + e.getMessage());
                }
            } finally {
                doneSignal.countDown();
            }
        });

        try {
            if (!doneSignal.await(timeoutMs, TimeUnit.MILLISECONDS)) {
                System.err.println("Master timed out waiting for tasks to complete.");
            }
        } catch (InterruptedException e) {
            System.err.println("Master interrupted.");
            Thread.currentThread().interrupt();
        }

        return foundNonPrime.get();
    }

    @Override
    public void close() {
        // Stop accepting new connections
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing server socket: " + e.getMessage());
            }
        }

        // Gracefully shutdown the worker pool.
        // The running WorkerHandlers will finish their current loop, send "STOP", and terminate.
        serverExecutor.shutdown();
        workerPool.shutdown();
        try {
            if (!serverExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                serverExecutor.shutdownNow();
            }
            // Wait for workers to finish gracefully
            if (!workerPool.awaitTermination(10, TimeUnit.SECONDS)) {
                System.err.println("Worker pool did not terminate in time. Forcing shutdown.");
                workerPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            serverExecutor.shutdownNow();
            workerPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private class WorkerHandler implements Runnable {
        private final Socket socket;
        private final CountDownLatch doneSignal;

        public WorkerHandler(Socket socket, CountDownLatch doneSignal) {
            this.socket = socket;
            this.doneSignal = doneSignal;
        }

        @Override
        public void run() {
            try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                while (!foundNonPrime.get() && completedTasks.get() < totalTasks) {
                    Task task = taskQueue.poll();
                    if (task != null) {
                        runningTasks.put(task.getTaskId(), task);
                        try {
                            out.writeUnshared(task);
                            out.flush();

                            Object obj = in.readObject();
                            if (obj instanceof Result) {
                                Result result = (Result) obj;
                                runningTasks.remove(task.getTaskId());
                                completedTasks.incrementAndGet();

                                if (result.hasNonPrime()) {
                                    foundNonPrime.set(true);
                                    doneSignal.countDown();
                                    break;
                                }

                                if (completedTasks.get() == totalTasks) {
                                    doneSignal.countDown();
                                    break;
                                }
                            }
                        } catch (IOException | ClassNotFoundException e) {
                            System.err.println("Worker connection lost, task " + task.getTaskId()
                                    + " failed: " + e.getMessage());
                            runningTasks.remove(task.getTaskId());
                            taskQueue.add(task);
                            break;
                        }
                    } else {
                        if (Thread.currentThread().isInterrupted()) {
                            break;
                        }
                        Thread.sleep(100);
                    }
                }

                try {
                    out.writeUnshared("STOP");
                    out.flush();
                } catch (IOException ignored) {
                }

            } catch (IOException | InterruptedException e) {
                if (!socket.isClosed()) {
                    System.err.println("Worker handler error: " + e.getMessage());
                }
            } finally {
                try {
                    socket.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}