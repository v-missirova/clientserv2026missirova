import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Decoder {
    public static Packet decode(byte[] encoded, byte[] key) throws Exception {

        // check if packet is valid
        ByteBuffer buffer = ByteBuffer.allocate(encoded.length);
        buffer.put(encoded);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.flip();

        byte bMagic = buffer.get();
        if (bMagic != 0x13) {
            throw new IllegalArgumentException("invalid magic byte");
        }

        byte bSrc = buffer.get();
        long pPktId = buffer.getLong();
        int wLen = buffer.getInt();
        short firstCrc16 = buffer.getShort();

        byte[] firstHalf = Arrays.copyOfRange(encoded, 0, 14);
        short actualFirstCrc16 = Crc16.calculateCrc(firstHalf);

        if (firstCrc16 != actualFirstCrc16) {
            throw new Exception("first CRC16 mismatch");
        }

        byte[] msg = new byte[wLen];
        buffer.get(msg);
        short secondCrc16 = buffer.getShort();

        short actualSecondCrc16 = Crc16.calculateCrc(msg);

        if (secondCrc16!=actualSecondCrc16) {
            throw new Exception("second CRC16 mismatch");
        }

        // decrypt
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] unencryptedMsg = cipher.doFinal(msg);
        ByteBuffer bufferMsg = ByteBuffer.allocate(unencryptedMsg.length);
        bufferMsg.put(unencryptedMsg);
        bufferMsg.order(ByteOrder.BIG_ENDIAN);
        bufferMsg.flip();

        int cType = bufferMsg.getInt();
        int bUserId = bufferMsg.getInt();
        byte[] msgBytes = new byte[bufferMsg.remaining()];
        bufferMsg.get(msgBytes);
        String messageText = new String(msgBytes, StandardCharsets.UTF_8);
        Message decodedMessage = new Message(cType, bUserId, messageText);

        return new Packet(bSrc, pPktId, decodedMessage);
    }
}
