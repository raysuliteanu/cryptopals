package kidoni;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

import static kidoni.CryptoUtils.toHexString;
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

    /*
    6    2    6    3
    0110 0010 0110 0011
    XOR
    6    1    6    1
    0110 0001 0110 0001
    EQUALS
    0    3    0    2
    0000 0011 0000 0010
    */
    @Test
    void xor2() throws IOException {
        // ascii a
        byte a = 0x61;
        // ascii b
        byte b = 0x62;
        // ascii c
        byte c = 0x63;

        String abc = toHexString(new byte[]{a, b, c});
        assertEquals("616263", abc);

        String input = "6263";
        String key = "61";
        String xor = CryptoUtils.xor(input, key);
        assertEquals("0302", xor);
    }

    @Test
    void keyMatchesByte() {
        assertTrue(CryptoUtils.keyMatchesByte((byte) 0x61, 'a'));
        assertFalse(CryptoUtils.keyMatchesByte((byte) 0x61, 'A'));
    }

    @Test
    void computeFrequencies() {
        String input = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Map<Character, Float> frequencies = CryptoUtils.computeFrequencies(input.getBytes(StandardCharsets.UTF_8));
        Set<Map.Entry<Character, Float>> entries = frequencies.entrySet();
        assertTrue(entries.stream().allMatch(entry -> {
            Float frequency = CryptoUtils.LETTER_FREQUENCIES.get(entry.getKey().toString().toLowerCase());
            return frequency.equals(entry.getValue());
        }));
    }
}