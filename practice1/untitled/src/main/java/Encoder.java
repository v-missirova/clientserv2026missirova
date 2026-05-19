import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Encoder {
    private static byte[] CipherMsg(byte[] unencriptedMsg, byte[] key) throws
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalStateException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(unencriptedMsg);
    }

    public static byte[] encode(Packet packet, byte[] key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] messageBytes = packet.bMsg.message.getBytes(StandardCharsets.UTF_8);
        int wLen = 8 + messageBytes.length;

        ByteBuffer serializeMsg = ByteBuffer.allocate(wLen);
        serializeMsg.order(ByteOrder.BIG_ENDIAN);

        serializeMsg.putInt(packet.bMsg.cType);
        serializeMsg.putInt(packet.bMsg.bUserId);
        serializeMsg.put(messageBytes);

        byte[] encryptedMsg = CipherMsg(serializeMsg.array(), key);
        wLen = encryptedMsg.length;

        ByteBuffer buffer = ByteBuffer.allocate(16 + wLen + 2);
        buffer.order(ByteOrder.BIG_ENDIAN);

        byte bMagic = 0x13;

        buffer.put(bMagic);
        buffer.put(packet.bSrc);
        buffer.putLong(packet.pPktId);
        buffer.putInt(wLen);

        byte[] bufferFist = Arrays.copyOfRange(buffer.array(), 0, 14);
        short wCrc16 = Crc16.calculateCrc(bufferFist);
        buffer.putShort(wCrc16);

        buffer.put(encryptedMsg);
        wCrc16 = Crc16.calculateCrc(encryptedMsg);
        buffer.putShort(wCrc16);

        return buffer.array();
    }
}
