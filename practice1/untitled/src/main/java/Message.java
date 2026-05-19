public class Message {
    public Message(int cType, int bUserId, String message) {
        this.cType = cType;
        this.bUserId = bUserId;
        this.message = message;
    }

    int cType;
    int bUserId;
    String message;
}