package practice3;

import practice1.Constants;
import practice1.Decoder;
import practice1.Encoder;
import practice1.Message;
import practice1.Packet;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.io.IOException;

public class StoreClientTCP {
    private final String host;
    private final int port;
    private Socket socket;
    private InputStream in;
    private OutputStream out;

    public StoreClientTCP(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private void connect() {
        while (true) {
            try {
                socket = new Socket(host, port);
                out = socket.getOutputStream();
                in = socket.getInputStream();
                System.out.println("Client: connected to the server");
                break;
            } catch (IOException e) {
                System.err.println("Client: server is down. Trying to reconnect");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    public void startSendingLoop(int clientId) {
        connect();

        long packetId = 1;
        while (true) {
            try {
                Message msg = new Message(10, clientId, "query " + packetId + " from client " + clientId);
                Packet packet = new Packet((byte) 1, packetId++, msg);

                byte[] encryptedData = Encoder.encode(packet, Constants.KEY);

                out.write(encryptedData);
                out.flush();
                System.out.println("Client " + clientId + ": packet sent.");

                byte[] buffer = new byte[1024];
                int bytesRead = in.read(buffer);

                if (bytesRead == -1) {
                    throw new IOException("Server closed connection.");
                }

                byte[] responseData = new byte[bytesRead];
                System.arraycopy(buffer, 0, responseData, 0, bytesRead);
                Packet responsePacket = Decoder.decode(responseData, Constants.KEY);

                System.out.println("Client " + clientId + ": Get answer: " + responsePacket.getbMsg().getMessage());

                Thread.sleep(2000);

            } catch (IOException e) {
                System.err.println("Client " + clientId + ": stopped connection!!!!.");
                closeResources();
                connect();
            } catch (Exception e) {
                System.err.println("Client " + clientId + ": Internal error: " + e.getMessage());
                try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
            }
        }
    }

    private void closeResources() {
        try { if (in != null) in.close(); } catch (IOException ignored) {}
        try { if (out != null) out.close(); } catch (IOException ignored) {}
        try { if (socket != null) socket.close(); } catch (IOException ignored) {}
    }
}