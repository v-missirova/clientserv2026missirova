package practice3;

import homework2.Sender;

import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class RealSender implements Runnable, Sender {
    private final BlockingQueue<NetworkContext> inputQueue;
    private volatile boolean isRunning = true;

    public RealSender(BlockingQueue<NetworkContext> inputQueue) {
        this.inputQueue = inputQueue;
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                NetworkContext context = inputQueue.take();
                send(context);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    @Override
    public void send(NetworkContext context) {
        try {
            byte[] dataToSend = context.getRawData();

            if (context.isUdp()) {
                try (DatagramSocket udpSocket = new DatagramSocket()) {
                    DatagramPacket packet = new DatagramPacket(
                            dataToSend, dataToSend.length,
                            context.getUdpAddress(), context.getUdpPort());
                    udpSocket.send(packet);
                    System.out.println("Server: UDP answer");
                }
            } else {
                Socket targetSocket = context.getClientSocket();
                if (targetSocket != null && !targetSocket.isClosed()) {
                    OutputStream out = targetSocket.getOutputStream();
                    out.write(dataToSend);
                    out.flush();
                    System.out.println("Server: TCP answer");
                }
            }
        } catch (Exception e) {
            System.err.println("error in sending " + e.getMessage());
        }
    }
}