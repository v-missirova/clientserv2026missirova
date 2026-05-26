package homework2;

import java.util.concurrent.BlockingQueue;
import practice1.Constants;
import practice1.Decoder;
import practice1.Packet;

public class Decriptor implements Runnable {
    private final BlockingQueue<byte[]> inputQueue;
    private final BlockingQueue<Packet> outputQueue;

    public Decriptor(BlockingQueue<byte[]> inputQueue, BlockingQueue<Packet> outputQueue) {
        this.inputQueue = inputQueue;
        this.outputQueue = outputQueue;
    }

    public void decript(byte[] message) {
        try {
            Packet decryptedMsg = Decoder.decode(message, Constants.KEY);
            outputQueue.put(decryptedMsg);
        }
        catch (Exception e) {
            System.err.println("error while decrypting the package: " + e.getMessage());
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void run() {
        boolean isRunning = true;
        while (isRunning) {
            try {
                byte[] message = inputQueue.take();
                decript(message);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}