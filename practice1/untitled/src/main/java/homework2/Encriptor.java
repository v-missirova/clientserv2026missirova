package homework2;

import java.util.concurrent.BlockingQueue;
import practice1.Constants;
import practice1.Encoder;
import practice1.Packet;

public class Encriptor implements Runnable {
    private final BlockingQueue<Packet> inputQueue;
    private final BlockingQueue<byte[]> outputQueue;
    private volatile boolean isRunning = true;

    public Encriptor(BlockingQueue<Packet> inputQueue, BlockingQueue<byte[]> outputQueue) {
        this.inputQueue = inputQueue;
        this.outputQueue = outputQueue;
    }

    public void encrypt(Packet packet) {
        try {
            byte[] encodedData = Encoder.encode(packet, Constants.KEY);
            outputQueue.put(encodedData);

        } catch (Exception e) {
            System.err.println("error while encrypting package (Encriptor.java) " + e.getMessage());
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                Packet packet = inputQueue.take();
                encrypt(packet);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}