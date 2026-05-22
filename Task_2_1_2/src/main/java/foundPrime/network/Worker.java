package foundPrime.network;

import foundPrime.distributed.PrimeFinder;
import foundPrime.distributed.Result;
import foundPrime.distributed.Task;

import java.io.IOException;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.UUID;

public class Worker {
    private final String host;
    private final int port;
    private final String workerId;

    public Worker(String host, int port) {
        this.host = host;
        this.port = port;
        this.workerId = UUID.randomUUID().toString();
    }

    public void start() {
        System.out.println("Worker " + workerId + " starting...");
        int failedAttempts = 0;
        int maxFailedAttempts = 10;

        while (failedAttempts < maxFailedAttempts) {
            try (Socket socket = new Socket(host, port);
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                 
                System.out.println("Worker " + workerId + " connected to master.");
                failedAttempts = 0;
                
                while (true) {
                    Object obj = in.readObject();
                    if (obj instanceof Task) {
                        Task task = (Task) obj;
                        System.out.println("Worker " + workerId + " received task " + task.getTaskId());
                        boolean hasNonPrime = checkNonPrime(task.getNumbers());
                        Result result = new Result(task.getTaskId(), hasNonPrime, workerId);
                        out.writeObject(result);
                        out.flush();
                        out.reset();
                    } else if (obj instanceof String && obj.equals("STOP")) {
                        System.out.println("Worker " + workerId + " received STOP command. Terminating.");
                        return;
                    }
                }
            } catch (EOFException e) {
                System.out.println("Worker " + workerId + " connection closed by master. Terminating gracefully.");
                return;
            } catch (IOException | ClassNotFoundException e) {
                failedAttempts++;
                System.err.println("Worker " + workerId + " error: " + e.getMessage() 
                        + ". Retrying (" + failedAttempts + "/" + maxFailedAttempts + ") in 3 seconds...");
                if (failedAttempts >= maxFailedAttempts) {
                    System.err.println("Worker " + workerId + " reached max retries. Terminating.");
                    return;
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    private boolean checkNonPrime(long[] numbers) {
        for (long n : numbers) {
            if (!PrimeFinder.isPrime(n)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "127.0.0.1";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 8080;
        new Worker(host, port).start();
    }
}
