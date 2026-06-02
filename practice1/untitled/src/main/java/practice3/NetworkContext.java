package practice3;

import practice1.Packet;

import java.net.InetAddress;
import java.net.Socket;

public class NetworkContext {
    private Socket clientSocket;

    private InetAddress udpAddress;
    private int udpPort;
    private boolean isUdp;

    public boolean isUdp() {
        return isUdp;
    }

    public void setUdp(boolean udp) {
        isUdp = udp;
    }

    public int getUdpPort() {
        return udpPort;
    }

    public void setUdpPort(int udpPort) {
        this.udpPort = udpPort;
    }

    public InetAddress getUdpAddress() {
        return udpAddress;
    }

    public void setUdpAddress(InetAddress udpAddress) {
        this.udpAddress = udpAddress;
    }

    private byte[] rawData;
    private Packet packet;

    public NetworkContext(InetAddress udpAddress, int udpPort, byte[] rawData) {
        this.udpAddress = udpAddress;
        this.udpPort = udpPort;
        this.rawData = rawData;
        this.isUdp = true;
    }

    public NetworkContext(Socket clientSocket, byte[] rawData) {
        this.clientSocket = clientSocket;
        this.rawData = rawData;
    }

    public byte[] getRawData() {
        return rawData;
    }

    public void setRawData(byte[] rawData) {
        this.rawData = rawData;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public Packet getPacket() {
        return packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }
}
