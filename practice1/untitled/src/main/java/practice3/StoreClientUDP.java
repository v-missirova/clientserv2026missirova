package practice3;

import practice1.Constants;
import practice1.Decoder;
import practice1.Encoder;
import practice1.Message;
import practice1.Packet;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class StoreClientUDP {
    private final String host;
    private final int port;

    public StoreClientUDP(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void startSendingLoop(int clientId) {
        long packetId = 1;
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress address = InetAddress.getByName(host);
            int TIMEOUT_MS = 2000;
            socket.setSoTimeout(TIMEOUT_MS);
            while (true) {
                Message msg = new Message(10, clientId, "UDP query " + packetId);
                Packet packet = new Packet((byte) 1, packetId++, msg);
                byte[] encryptedData = Encoder.encode(packet, Constants.KEY);

                boolean success = false;

                int MAX_RETRIES = 3;
                for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
                    try {
                        DatagramPacket sendPacket = new DatagramPacket(encryptedData, encryptedData.length, address, port);
                        socket.send(sendPacket);
                        System.out.println("UDP client " + clientId + ": send packet (attempt: " + attempt + ")");
                        byte[] receiveBuffer = new byte[1024];
                        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                        socket.receive(receivePacket);
                        byte[] responseData = new byte[receivePacket.getLength()];
                        System.arraycopy(receivePacket.getData(), 0, responseData, 0, receivePacket.getLength());
                        Packet responsePacket = Decoder.decode(responseData, Constants.KEY);
                        System.out.println("UDPclient " + clientId + ": OK, answer received: " + responsePacket.getbMsg().getMessage());
                        success = true;
                        break;
                    } catch (SocketTimeoutException e) {
                        System.err.println("UDP client " + clientId + ": time out, packet had been lost, retrying..");
                    }
                }

                if (!success) {
                    System.err.println("UDP client " + clientId + ": server had not answered after " + MAX_RETRIES + " attempts.");
                }

                Thread.sleep(3000);
            }
        } catch (Exception e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }
}