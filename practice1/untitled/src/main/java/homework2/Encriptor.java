package homework2;

import java.util.concurrent.BlockingQueue;
import practice1.Constants;
import practice1.Encoder;
import practice3.NetworkContext;

public class Encriptor implements Runnable {
    private final BlockingQueue<NetworkContext> inputQueue;
    private final BlockingQueue<NetworkContext> outputQueue;
    private volatile boolean isRunning = true;

    public Encriptor(BlockingQueue<NetworkContext> inputQueue, BlockingQueue<NetworkContext> outputQueue) {
        this.inputQueue = inputQueue;
        this.outputQueue = outputQueue;
    }

    public void encrypt(NetworkContext context) {
        try {
            byte[] encodedData = Encoder.encode(context.getPacket(), Constants.KEY);
            context.setRawData(encodedData);
            outputQueue.put(context);
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
                NetworkContext context = inputQueue.take();
                encrypt(context);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}