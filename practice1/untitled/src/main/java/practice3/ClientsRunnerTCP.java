package practice3;

public class ClientsRunnerTCP {
    static void main(String[] args) {
        System.out.println("tcp client simulator");
        int numberOfClients = 3;

        for (int i = 1; i <= numberOfClients; i++) {
            final int clientId = i;
            new Thread(() -> {
                StoreClientTCP client = new StoreClientTCP("localhost", 8080);
                client.startSendingLoop(clientId);
            }).start();
        }
    }
}