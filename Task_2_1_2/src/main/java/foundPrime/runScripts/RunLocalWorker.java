package foundPrime.runScripts;

import foundPrime.network.Worker;

public class RunLocalWorker {
    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 8080;
        
        System.out.println("Starting local Worker...");
        Worker worker = new Worker(host, port);
        worker.start();
    }
}
