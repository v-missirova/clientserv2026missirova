package homework2;

import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import org.apache.commons.codec.binary.Hex;

public class FakeSender implements Sender, Runnable {
    private final BlockingQueue<byte[]> inputQueue;

    public FakeSender(BlockingQueue<byte[]> inputQueue) {
        this.inputQueue = inputQueue;
    }

    @Override
    public void sendMessage(byte[] message, InetAddress target) {
        System.out.println("address: " + target);
        System.out.println("packet length: " + message.length + " bytes");
        System.out.println("data (hex): " + Hex.encodeHexString(message) + "\n\n");
    }

    @Override
    public void run() {
        boolean isRunning = true;
        while (isRunning) {
            try {
                byte[] dataToSend = inputQueue.take();
                InetAddress target = InetAddress.getLocalHost();
                sendMessage(dataToSend, target);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("error while sending (FakeSender.java) " + e.getMessage());
            }
        }
    }
}