package practice3;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

public class StoreServerTCP implements Runnable {
    private final int port;
    private final BlockingQueue<NetworkContext> inputQueue;
    private final ExecutorService clientHandlersPool;
    private volatile boolean isRunning = true;

    public StoreServerTCP(int port, BlockingQueue<NetworkContext> inputQueue, ExecutorService pool) {
        this.port = port;
        this.inputQueue = inputQueue;
        this.clientHandlersPool = pool;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("TCP server is ready to get msg on port " + port);
            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("new client connected: " + clientSocket.getInetAddress());
                clientHandlersPool.execute(new ClientHandler(clientSocket, inputQueue));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private record ClientHandler(Socket socket, BlockingQueue<NetworkContext> queue) implements Runnable {
        @Override
            public void run() {
                try (InputStream in = socket.getInputStream()) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        byte[] data = new byte[bytesRead];
                        System.arraycopy(buffer, 0, data, 0, bytesRead);
                        queue.put(new NetworkContext(socket, data));
                    }
                } catch (Exception e) {
                    System.out.println("Client disconnected.");
                }
            }
        }
}