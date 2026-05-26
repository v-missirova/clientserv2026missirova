package homework2;

import practice1.Packet;

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
        BlockingQueue<byte[]> queue1 = new LinkedBlockingQueue<>(100);
        BlockingQueue<Packet> queue2 = new LinkedBlockingQueue<>(100);
        BlockingQueue<Packet> queue3 = new LinkedBlockingQueue<>(100);
        BlockingQueue<byte[]> queue4 = new LinkedBlockingQueue<>(100);

        ExecutorService receivers = Executors.newFixedThreadPool(4);
        ExecutorService decryptors = Executors.newFixedThreadPool(3);
        ExecutorService processors = Executors.newFixedThreadPool(4);
        ExecutorService encryptors = Executors.newFixedThreadPool(3);
        ExecutorService senders  = Executors.newFixedThreadPool(5);

        pools.add(receivers);
        pools.add(decryptors);
        pools.add(processors);
        pools.add(encryptors);
        pools.add(senders);

        for (int i = 0; i < 4; i++) receivers.execute(new FakeReceiver(queue1));
        for (int i = 0; i < 3; i++) decryptors.execute(new Decriptor(queue1, queue2));
        for (int i = 0; i < 4; i++) processors.execute(new Processor(queue2, queue3));
        for (int i = 0; i < 3; i++) encryptors.execute(new Encriptor(queue3, queue4));
        for (int i = 0; i < 5; i++) senders.execute(new FakeSender(queue4));
    }

    public void stop() {
        for (ExecutorService pool : pools) {
            pool.shutdownNow();
        }
        for (ExecutorService pool : pools) {
            try {
                if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.err.println("treads pool couldn't stop in time.");
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