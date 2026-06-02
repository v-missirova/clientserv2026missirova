package homework2;

import java.util.concurrent.BlockingQueue;
import practice1.Message;
import practice1.Packet;
import practice3.NetworkContext;

public class Processor implements Runnable {

    private final BlockingQueue<NetworkContext> inputQueue;
    private final BlockingQueue<NetworkContext> outputQueue;

    public Processor(BlockingQueue<NetworkContext> inputQueue, BlockingQueue<NetworkContext> outputQueue) {
        this.inputQueue = inputQueue;
        this.outputQueue = outputQueue;
    }

    public void process(NetworkContext context) {
        try {
            Packet packet = context.getPacket();
            int cType = packet.getbMsg().getcType();
            int userId = packet.getbMsg().getbUserId();
            long packetId = packet.getpPktId();
            Packet replyPacket = new Packet((byte) 2, packetId,
                    new Message(cType, userId, "OK, PACKET RECEIVED"));
            context.setPacket(replyPacket);
            outputQueue.put(context);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("error while processing package (Processor.java): " + e.getMessage());
        }
    }

    @Override
    public void run() {
        boolean isRunning = true;
        while (isRunning) {
            try {
                NetworkContext context = inputQueue.take();
                process(context);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}