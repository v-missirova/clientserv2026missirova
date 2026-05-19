public class Packet {
    public Packet(byte bSrc, long pPktId, Message bMsg) {
        this.bSrc = bSrc;
        this.pPktId = pPktId;
        this.bMsg = bMsg;
    }

    byte bSrc;
    long pPktId;
    Message bMsg;
}