package homework2;
import java.util.concurrent.BlockingQueue;
import java.util.Random;

import practice1.Constants;
import practice1.Encoder;
import practice1.Message;
import practice1.Packet;

public class FakeReceiver implements Receiver, Runnable {
    private final BlockingQueue<byte[]> outputQueue;
    private volatile boolean isRunning = true;
    private final Random random = new Random();
    private long packetIdCounter = 1;

    public FakeReceiver(BlockingQueue<byte[]> outputQueue) {
        this.outputQueue = outputQueue;
    }

    @Override
    public void receiveMessage() {
        try {
            int randomCommand = random.nextInt(6) + 1;
            int randomAmount = random.nextInt(50) + 1;
            Packet packet = new Packet((byte) 1, packetIdCounter++, new Message(randomCommand, 1, String.valueOf(randomAmount)));
            byte[] encoded = Encoder.encode(packet, Constants.KEY);
            outputQueue.put(encoded);
        } catch (Exception e) {
            System.err.println("error while creating the package: " + e.getMessage());
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            receiveMessage();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void stop() {
        isRunning = false;
    }
}