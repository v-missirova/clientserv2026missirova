package practice1;
public class Message {
    public String getMessage() {
        return message;
    }

    public int getbUserId() {
        return bUserId;
    }

    public int getcType() {
        return cType;
    }

    public Message(int cType, int bUserId, String message) {
        this.cType = cType;
        this.bUserId = bUserId;
        this.message = message;
    }

    int cType;
    int bUserId;
    String message;
}