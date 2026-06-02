package homework2;

import practice3.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Executor {
    private final List<ExecutorService> pools = new ArrayList<>();

    public void start() {
        BlockingQueue<NetworkContext> queue1 = new LinkedBlockingQueue<>(100);
        BlockingQueue<NetworkContext> queue2 = new LinkedBlockingQueue<>(100);
        BlockingQueue<NetworkContext> queue3 = new LinkedBlockingQueue<>(100);
        BlockingQueue<NetworkContext> queue4 = new LinkedBlockingQueue<>(100);

        ExecutorService receivers = Executors.newFixedThreadPool(4);
        ExecutorService decryptors = Executors.newFixedThreadPool(3);
        ExecutorService processors = Executors.newFixedThreadPool(4);
        ExecutorService encryptors = Executors.newFixedThreadPool(3);
        ExecutorService senders  = Executors.newFixedThreadPool(7);

        pools.add(receivers);
        pools.add(decryptors);
        pools.add(processors);
        pools.add(encryptors);
        pools.add(senders);

        StoreServerTCP tcpServer = new StoreServerTCP(8080, queue1, receivers);
        new Thread(tcpServer).start();

        StoreServerUDP udpServer = new StoreServerUDP(8081, queue1);
        new Thread(udpServer).start();

        for (int i = 0; i < 3; i++) decryptors.execute(new Decriptor(queue1, queue2));
        for (int i = 0; i < 4; i++) processors.execute(new Processor(queue2, queue3));
        for (int i = 0; i < 3; i++) encryptors.execute(new Encriptor(queue3, queue4));

        for (int i = 0; i < 5; i++) senders.execute(new RealSender(queue4));
    }

    public void stop() {
        for (ExecutorService pool : pools) {
            pool.shutdownNow();
        }
        for (ExecutorService pool : pools) {
            try {
                if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.err.println("threads pool couldn't stop in time.");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    static void main(String[] args) {
        Executor server = new Executor();
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        server.start();
    }
}