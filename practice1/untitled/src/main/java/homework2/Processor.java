package homework2;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import practice1.Message;
import practice1.Packet;
import practice3.NetworkContext;
import practice4.Filter;
import practice4.Product;
import practice4.ProductGroup;
import practice4.StoreDB;

public class Processor implements Runnable {

    private final BlockingQueue<NetworkContext> inputQueue;
    private final BlockingQueue<NetworkContext> outputQueue;
    private final StoreDB db;

    public Processor(BlockingQueue<NetworkContext> inputQueue, BlockingQueue<NetworkContext> outputQueue, StoreDB db) {
        this.inputQueue = inputQueue;
        this.outputQueue = outputQueue;
        this.db = db;
    }

    public void process(NetworkContext context) {
        try {
            Packet packet = context.getPacket();
            int cType = packet.getbMsg().getcType();
            int userId = packet.getbMsg().getbUserId();
            long packetId = packet.getpPktId();
            String payload = packet.getbMsg().getMessage();
            String responseText;

            try {
                switch (cType) {
                    case 1:
                        int pId = Integer.parseInt(payload.trim());
                        int qty = db.getProductQuantity(pId);
                        responseText = "Product quantity " + pId + ": " + qty;
                        break;

                    case 2:
                        String[] qtyArgs = payload.split(",");
                        int qId = Integer.parseInt(qtyArgs[0].trim());
                        int amount = Integer.parseInt(qtyArgs[1].trim());
                        boolean qSuccess = db.updateQuantity(qId, amount);
                        responseText = qSuccess ? "Quantity updated" : "Error: couldn't find product";
                        break;

                    case 3:
                        ProductGroup group = new ProductGroup(payload.trim());
                        int groupId = db.insertGroup(group);
                        responseText = "Group created successfully with id: " + groupId;
                        break;

                    case 4:
                        String[] pArgs = payload.split(",");
                        Product newProduct = new Product(
                                pArgs[0].trim(),
                                Integer.parseInt(pArgs[1].trim()),
                                Double.parseDouble(pArgs[2].trim()),
                                Integer.parseInt(pArgs[3].trim())
                        );
                        int newProductId = db.insertProduct(newProduct);
                        responseText = "Product created successfully with id: " + newProductId;
                        break;

                    case 5:
                        String[] priceArgs = payload.split(",");
                        int priceId = Integer.parseInt(priceArgs[0].trim());
                        double newPrice = Double.parseDouble(priceArgs[1].trim());
                        boolean pSuccess = db.setPrice(priceId, newPrice);
                        responseText = pSuccess ? "Price updated" : "Error while price updating";
                        break;

                    case 6:
                        String[] fArgs = payload.split(",");
                        Filter filter = new Filter.FilterBuilder()
                                .name(fArgs[0].trim())
                                .limit(Integer.parseInt(fArgs[1].trim()))
                                .offset(Integer.parseInt(fArgs[2].trim()))
                                .build();

                        List<Product> results = db.searchProducts(filter);
                        responseText = "Products found: " + results.size() + ".\n" + results.toString();
                        break;
                    case 7:
                        int delId = Integer.parseInt(payload.trim());
                        boolean dSuccess = db.deleteProduct(delId);
                        responseText = dSuccess ? "Product deleted" : "Error while deleting the product";
                        break;
                    default:
                        responseText = "Error: unknow cType " + cType + ")";
                        break;
                }
            } catch (Exception e) {
                responseText = "Error: " + e.getMessage();
            }
            Packet replyPacket = new Packet((byte) 2, packetId,
                    new Message(cType, userId, responseText));

            context.setPacket(replyPacket);
            outputQueue.put(context);
        }
        catch (InterruptedException e) {
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