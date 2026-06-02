package practice3;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.BlockingQueue;

public class StoreServerUDP implements Runnable {
    private final int port;
    private final BlockingQueue<NetworkContext> inputQueue;
    private volatile boolean isRunning = true;

    public StoreServerUDP(int port, BlockingQueue<NetworkContext> inputQueue) {
        this.port = port;
        this.inputQueue = inputQueue;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("UDP server listens the port " + port);
            byte[] buffer = new byte[1024];

            while (isRunning) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                byte[] data = new byte[packet.getLength()];
                System.arraycopy(packet.getData(), 0, data, 0, packet.getLength());

                if (Math.random() > 0.8) {
                    System.out.println("lost packet (intentionally)");
                } else {
                    inputQueue.put(new NetworkContext(packet.getAddress(), packet.getPort(), data));
                }
            }
        } catch (Exception e) {
            System.err.println("UDP server error: " + e.getMessage());
        }
    }
}