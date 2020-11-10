package kidoni;

import java.io.IOException;
import java.util.Optional;

import static kidoni.CryptoUtils.decrypt;

public class XorDecrypt {

    public static void main(String[] args) throws IOException {
        String input = args.length == 1 ? args[0] :
                "1b37373331363f78151b7f2b783431333d78397828372d363c78373e783a393b3736";
        Optional<String> xor = decrypt(input);
        System.out.println(new String(CryptoUtils.parseHexString(xor.orElse("failed to decrypt"))));
    }
}
