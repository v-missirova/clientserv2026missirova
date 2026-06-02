package practice3;

public class ClientRunnerUDP {
    static void main(String[] args) {
        System.out.println("udp client simulator");
        int numberOfClients = 4;

        for (int i = 1; i <= numberOfClients; i++) {
            final int clientId = i;
            new Thread(() -> {
                StoreClientUDP client = new StoreClientUDP("localhost", 8081);
                client.startSendingLoop(clientId);
            }).start();
        }
    }
}
