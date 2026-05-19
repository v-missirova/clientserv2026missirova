import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class DecoderTest {
    @Test
    void TestDecode() throws Exception {
        String hexString = "13130000000000000001000000209815d7b2364f51a073eddc34f7668caba573e7c4f9a716fcc7a3af9a4902d3fccc7b5f17";

        byte[] data = Hex.decodeHex(hexString);
        Packet decodedPacket = Decoder.decode(data, Constants.KEY);

        Assertions.assertEquals((byte) 0x13, decodedPacket.bSrc, "bSrc is wrong");
        Assertions.assertEquals(1, decodedPacket.pPktId, "pPktId is wrong");
        Assertions.assertEquals(2, decodedPacket.bMsg.cType, "cType is wrong");
        Assertions.assertEquals(3, decodedPacket.bMsg.bUserId, "bUserId is wrong");
        Assertions.assertEquals("cheeseburger", decodedPacket.bMsg.message, "message is wrong");
    }
}