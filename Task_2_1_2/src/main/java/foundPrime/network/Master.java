package foundPrime.network;

import foundPrime.distributed.Result;
import foundPrime.distributed.Task;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;

public class Master {
    private final int port;
    private final int timeoutMs;

    private final ConcurrentLinkedQueue<Task> taskQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentHashMap<Integer, Task> runningTasks = new ConcurrentHashMap<>();
    
    private final AtomicBoolean foundNonPrime = new AtomicBoolean(false);
    private final AtomicInteger completedTasks = new AtomicInteger(0);
    private int totalTasks;

    public Master(int port, int timeoutMs) {
        this.port = port;
        this.timeoutMs = timeoutMs;
    }
    
    public boolean processTasks(List<Task> tasks) {
        this.totalTasks = tasks.size();
        this.taskQueue.addAll(tasks);
        
        CountDownLatch doneSignal = new CountDownLatch(1);
        ExecutorService serverExecutor = Executors.newSingleThreadExecutor();
        ExecutorService workerPool = Executors.newCachedThreadPool();

        serverExecutor.submit(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("Master started on port " + port);
                
                while (!foundNonPrime.get() && completedTasks.get() < totalTasks) {
                    serverSocket.setSoTimeout(1000);
                    try {
                        Socket workerSocket = serverSocket.accept();
                        System.out.println("Accepted worker from " + workerSocket.getInetAddress());
                        workerPool.submit(new WorkerHandler(workerSocket, doneSignal));
                    } catch (java.net.SocketTimeoutException e) {}
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                doneSignal.countDown();
            }
        });

        try {
            doneSignal.await(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            System.err.println("Master interrupted.");
            Thread.currentThread().interrupt();
        }

        serverExecutor.shutdownNow();
        workerPool.shutdownNow();

        return foundNonPrime.get();
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
                            out.writeObject(task);
                            out.flush();
                            out.reset();

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
                        Thread.sleep(100);
                    }
                }
                
                try {
                    out.writeObject("STOP");
                    out.flush();
                } catch (IOException ignored) {}

            } catch (IOException | InterruptedException e) {
                System.err.println("Worker handler error: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException ignored) {}
            }
        }
    }
}
