package kidoni;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public abstract class Base64Utils {

    public static byte[] toBase64(InputStream input) throws IOException {
        byte[] buffer = CryptoUtils.parseHexString(input);

        return Base64.getEncoder().encode(buffer);
    }

    public static byte[] toBase64(final String input) throws IOException {
        return toBase64(input, StandardCharsets.UTF_8);
    }

    public static byte[] toBase64(String input, Charset charset) throws IOException {
        return toBase64(new ByteArrayInputStream(input.getBytes(charset)));
    }

}
