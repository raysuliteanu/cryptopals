package kidoni;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static kidoni.CryptoUtils.decrypt;
import static kidoni.CryptoUtils.parseHexString;

public class XorDecryptFile {

    public static void main(String[] args) throws IOException {
        String filename = args.length == 1 ? args[0] : "classpath:/set1-ex4.txt";
        String[] parts = filename.split(":");
        String type = "file";
        if (parts.length == 1) {
            filename = parts[0];
        }
        else if (parts.length == 2) {
            type = parts[0];
            filename = parts[1];
        }

        try (BufferedReader bufferedReader = getReader(type, filename)) {
            AtomicInteger lineNumber = new AtomicInteger(1);
            bufferedReader.lines().forEach(line -> {
                try {
                    System.out.printf("%d: %s ... ", lineNumber.get(), line);
                    Optional<String> xor = decrypt(line);
                    String hexResult = xor.orElse(null);
                    if (hexResult != null) {
                        String result = new String(parseHexString(hexResult));
                        System.out.printf("%s\n", result);
                    }
                    else {
                        System.out.println("failed to decrypt line number " + lineNumber + ": " + line);
                    }
                }
                catch (Throwable e) {
                    System.out.println(e.getMessage());
                }
                finally {
                    lineNumber.incrementAndGet();
                }
            });
        }
    }

    private static BufferedReader getReader(String type, String filename) throws FileNotFoundException {
        BufferedReader reader;

        if (type.equals("file")) {
            reader = new BufferedReader(new FileReader(filename));
        }
        else if (type.equals("classpath")) {
            reader = new BufferedReader(new InputStreamReader(XorDecryptFile.class.getResourceAsStream(filename)));
        }
        else {
            throw new IllegalArgumentException("unsupported file type: " + type);
        }

        return reader;
    }
}
