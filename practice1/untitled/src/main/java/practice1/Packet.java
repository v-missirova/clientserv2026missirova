package practice1;
public class Packet {
    public byte getbSrc() {
        return bSrc;
    }

    public long getpPktId() {
        return pPktId;
    }

    public Message getbMsg() {
        return bMsg;
    }

    public Packet(byte bSrc, long pPktId, Message bMsg) {
        this.bSrc = bSrc;
        this.pPktId = pPktId;
        this.bMsg = bMsg;
    }

    byte bSrc;
    long pPktId;
    Message bMsg;
}