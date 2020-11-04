package kidoni;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class CryptoUtilsTest {
    public static final String INPUT1 = "1c0111001f010100061a024b53535009181c";
    public static final String INPUT2 = "686974207468652062756c6c277320657965";
    public static final String RESULT = "746865206b696420646f6e277420706c6179";

    @Test
    public void xor() throws IOException {
        String xor = CryptoUtils.xor(INPUT1, INPUT2);
        assertEquals(RESULT, xor);
    }

    @Test
    public void readFully() throws IOException {
        byte[] result = CryptoUtils.readFully(new ByteArrayInputStream(INPUT1.getBytes(StandardCharsets.UTF_8)));
        assertArrayEquals(INPUT1.getBytes(), result);

        byte[] bytes = (INPUT1 + INPUT2).getBytes(StandardCharsets.UTF_8);
        result = CryptoUtils.readFully(new ByteArrayInputStream(bytes));
        assertArrayEquals(bytes, result);
    }

    @Test
    public void hexCharToByte() {
        String hexChars = "0123456789ABCDEFabcdef";
        byte[] hexBytes = {0x0, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9, 0xa, 0xb, 0xc, 0xd, 0xe, 0xf, 0xa, 0xb, 0xc, 0xd, 0xe, 0xf};
        for (int i = 0; i < hexChars.length(); i++) {
            assertEquals(hexBytes[i], CryptoUtils.hexCharToByte(hexChars.charAt(i)));
        }
    }
}