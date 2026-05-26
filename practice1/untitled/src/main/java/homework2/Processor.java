package homework2;

import java.util.concurrent.BlockingQueue;
import practice1.Message;
import practice1.Packet;

public class Processor implements Runnable {

    private final BlockingQueue<Packet> inputQueue;
    private final BlockingQueue<Packet> outputQueue;

    public Processor(BlockingQueue<Packet> inputQueue, BlockingQueue<Packet> outputQueue) {
        this.inputQueue = inputQueue;
        this.outputQueue = outputQueue;
    }

    public void process(Packet packet) {
        try {
            int cType = packet.getbMsg().getcType();
            int userId = packet.getbMsg().getbUserId();
            long packetId = packet.getpPktId();
            Packet replyPacket = new Packet((byte) 2, packetId,
                    new Message(cType, userId, "OK, PACKET RECEIVED"));
            outputQueue.put(replyPacket);
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
                Packet packet = inputQueue.take();
                process(packet);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}