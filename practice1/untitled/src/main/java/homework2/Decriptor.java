package homework2;

import java.util.concurrent.BlockingQueue;
import practice1.Constants;
import practice1.Decoder;
import practice1.Packet;
import practice3.NetworkContext;

public class Decriptor implements Runnable {
    private final BlockingQueue<NetworkContext> inputQueue;
    private final BlockingQueue<NetworkContext> outputQueue;

    public Decriptor(BlockingQueue<NetworkContext> inputQueue, BlockingQueue<NetworkContext> outputQueue) {
        this.inputQueue = inputQueue;
        this.outputQueue = outputQueue;
    }

    public void decript(NetworkContext context) {
        try {
            Packet decryptedMsg = Decoder.decode(context.getRawData(), Constants.KEY);
            context.setPacket(decryptedMsg);
            outputQueue.put(context);
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
                NetworkContext context = inputQueue.take();
                decript(context);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}