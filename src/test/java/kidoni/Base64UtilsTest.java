package kidoni;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Base64UtilsTest {

    public static final String INPUT = "49276d206b696c6c696e6720796f757220627261696e206c696b65206120706f69736f6e6f7573206d757368726f6f6d";
    public static final String ENCODED = "SSdtIGtpbGxpbmcgeW91ciBicmFpbiBsaWtlIGEgcG9pc29ub3VzIG11c2hyb29t";

    @Test
    public void base64() throws IOException {
        byte[] result = Base64Utils.toBase64(new ByteArrayInputStream(INPUT.getBytes(StandardCharsets.UTF_8)));
        assertArrayEquals(ENCODED.getBytes(StandardCharsets.UTF_8), result);

        result = Base64Utils.toBase64(INPUT);
        assertArrayEquals(ENCODED.getBytes(StandardCharsets.UTF_8), result);

        result = Base64Utils.toBase64(INPUT, StandardCharsets.UTF_8);
        assertArrayEquals(ENCODED.getBytes(StandardCharsets.UTF_8), result);
    }
}