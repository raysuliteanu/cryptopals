package kidoni;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class CryptoUtils {
    public static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final int BITS_PER_HEX_DIGIT = 4;

    public static final Map<String, Float> LETTER_FREQUENCIES = new HashMap<>();

    static {
        InputStream stream = CryptoUtils.class.getResourceAsStream("/letter-frequency.csv");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            reader.lines()
                    .map(s -> s.split(","))
                    .forEach(strings -> {
                        String letter = strings[0];
                        Float frequency = Float.parseFloat(strings[1]);
                        LETTER_FREQUENCIES.put(letter, frequency);
                    });
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String xor(String input, String key) throws IOException {
        assert input.length() % 2 == 0 : "invalid input length for input " + input;
        assert key.length() % 2 == 0 : "invalid input length for input " + key;
        assert input.length() >= key.length() : "input size must be greater than or equal to the key size";

        BitSet inputBitSet = BitSet.valueOf(parseHexString(input));
        BitSet keyBitSet = BitSet.valueOf(parseHexString(key));

        StringBuilder buffer = new StringBuilder();

        // each character of the string represents a hex digit which is 4 bits
        int keySizeBits = key.length() * BITS_PER_HEX_DIGIT;
        int inputSizeBits = input.length() * BITS_PER_HEX_DIGIT;
        for (int i = 0; i < inputSizeBits; i += keySizeBits) {
            BitSet bitSet = inputBitSet.get(i, i + keySizeBits);
            bitSet.xor(keyBitSet);
            byte[] bytes = bitSet.toByteArray();
            for (byte aByte : bytes) {
                if (aByte >= 0 && aByte <= 0xf) {
                    // pad with "0" e.g. we want "03" not "3"
                    buffer.append(Integer.toHexString(0x0));
                }
                buffer.append(Integer.toHexString(aByte));
            }
        }

        return buffer.toString();
    }

    static byte[] parseHexString(String input) throws IOException {
        return parseHexString(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
    }

    static byte[] parseHexString(InputStream input) throws IOException {
        byte[] originalInput = readFully(input);
        assert originalInput.length % 2 == 0 :
                "invalid input size " + originalInput.length + " for input " + new String(originalInput);

        byte[] buffer = new byte[originalInput.length / 2];

        for (int i = 0, j = 0; i < originalInput.length; j++) {
            byte high = hexCharToByte((char) originalInput[i++]);
            byte low = hexCharToByte((char) originalInput[i++]);
            buffer[j] = (byte) ((high << BITS_PER_HEX_DIGIT) | low);
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

    static String toHexString(byte[] bytes) {
        StringBuilder buffer = new StringBuilder();
        for (byte aByte : bytes) {
            buffer.append(String.format("%x", aByte));
        }
        return buffer.toString();
    }

    static Optional<String> decrypt(String input) throws IOException {
        byte[] hexBytes = parseHexString(input);

        Map<Character, Float> frequencies = computeFrequencies(hexBytes);

        Optional<Map.Entry<Character, Float>> max = frequencies.entrySet().stream().max(Map.Entry.comparingByValue());

        if (max.isPresent()) {
            Map.Entry<Character, Float> entry = max.get();
            Character key = entry.getKey();
            return Optional.of(xor(input, toHexString(key.toString().toUpperCase().getBytes(StandardCharsets.UTF_8))));
        }

        return Optional.empty();
    }

    static Map<Character, Float> computeFrequencies(byte[] hexBytes) {
        Map<Character, Float> frequencies = new HashMap<>();

        for (int i = 0; i < CHARACTERS.length(); i++) {
            char key = CHARACTERS.charAt(i);
            frequencies.put(key, scoreCharacter(hexBytes, key));
        }

        printFrequencies(frequencies);

        return frequencies;
    }

    // how often does 'key' appear in 'hexBytes'
    static Float scoreCharacter(byte[] hexBytes, Character key) {
        Float frequency = 0.0f;

        for (byte hexByte : hexBytes) {
            if (keyMatchesByte(hexByte, key)) {
                frequency += LETTER_FREQUENCIES.get(key.toString().toLowerCase());
            }
        }

        return frequency;
    }

    // value XOR-ed with itself is 0
    static boolean keyMatchesByte(byte hexByte, char key) {
        return (hexByte ^ key) == 0;
    }

    static void printFrequencies(Map<Character, Float> frequencies) {
        frequencies.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach(System.out::println);
    }

    static void printBinaryString(String hexString) {
        System.out.println(new BigInteger(hexString, 16).toString(2));
    }
}
