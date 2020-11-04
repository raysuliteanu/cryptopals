package kidoni;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;

public abstract class CryptoUtils {
    public static String xor(String first, String second) throws IOException {
        BitSet firstBitSet = BitSet.valueOf(parseHexString(first));
        BitSet secondBitSet = BitSet.valueOf(parseHexString(second));
        firstBitSet.xor(secondBitSet);

        StringBuilder buffer = new StringBuilder();
        byte[] bytes = firstBitSet.toByteArray();
        for (byte aByte : bytes) {
            buffer.append(Integer.toHexString(aByte));
        }

        return buffer.toString();
    }

    static byte[] parseHexString(String input) throws IOException {
        return parseHexString(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
    }

    static byte[] parseHexString(InputStream input) throws IOException {
        byte[] originalInput = readFully(input);
        assert originalInput.length % 2 == 0 : "invalid input size " + originalInput.length;
        byte[] buffer = new byte[originalInput.length / 2];

        int j = 0;
        for (int i = 0; i < originalInput.length; ) {
            byte high = hexCharToByte((char) originalInput[i++]);
            byte low = hexCharToByte((char) originalInput[i++]);
            buffer[j++] = (byte) ((high << 4) | low);
        }

        return buffer;
    }

    static byte hexCharToByte(char c) {
        if (c >= '0' && c <= '9') return (byte) (c - '0');
        if (c >= 'A' && c <= 'F') return (byte) ((c - 'A') + 10);
        if (c >= 'a' && c <= 'f') return (byte) ((c - 'a') + 10);

        throw new IllegalArgumentException("invalid byte: " + c);
    }

    static byte[] readFully(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        long read = inputStream.transferTo(outputStream);
        byte[] bytes = outputStream.toByteArray();
        assert read == bytes.length : "byte array size should equal read bytes";
        return bytes;
    }
}
