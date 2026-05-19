import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;


class EncoderTest {
    @Test
    void TheSameEncode()
            throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException,
            BadPaddingException, InvalidKeyException {
        Packet packet = new Packet((byte)0x13, 1,
                new Message(2, 3, "cheeseburger"));
        byte[] encoded = Encoder.encode(packet, Constants.KEY);
        Assertions.assertEquals("13130000000000000001000000209815d7b2364f51a073eddc34f7668caba573e7c4f9a716fcc7a3af9a4902d3fccc7b5f17", Hex.encodeHexString(encoded));
    }

    @Test
    void Encode() throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException,
            BadPaddingException, InvalidKeyException {

        Packet packet = new Packet((byte) 5, 100L, new Message(1, 12345, "double cheeseburger15"));

        byte[] result = Encoder.encode(packet, Constants.KEY);
        assertNotNull(result, "res shouldn't be null");
        assertEquals(0x13, result[0], "magic byte should equal to 0x13");
        assertEquals(5, result[1], "bSrc should be 5");

        ByteBuffer buffer = ByteBuffer.wrap(result);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.position(10);
        int wLen = buffer.getInt();
        int expectedTotalLength = 16 + wLen + 2;
        assertEquals(expectedTotalLength, result.length, "error in expected length");

        buffer.position(14);
        short writtenHeaderCrc = buffer.getShort();
        byte[] headerBytes = new byte[14];
        System.arraycopy(result, 0, headerBytes, 0, 14);
        short expectedHeaderCrc = Crc16.calculateCrc(headerBytes);
        assertEquals(expectedHeaderCrc, writtenHeaderCrc, "error in first crc16");

        byte[] encryptedMsgFromPacket = new byte[wLen];
        buffer.get(encryptedMsgFromPacket);
        short writtenMsgCrc = buffer.getShort();
        short expectedMsgCrc = Crc16.calculateCrc(encryptedMsgFromPacket);

        assertEquals(expectedMsgCrc, writtenMsgCrc, "error in second crc16");
        assertFalse(buffer.hasRemaining(), "there's bytes after second crc16!!");

    }
}