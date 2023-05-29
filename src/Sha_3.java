import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Arrays;

public class Sha_3 {

    public static final byte[] ZERO_RIGHT_ENCODED = {(byte) 0x00, (byte) 0x01};
    public static final byte[] ZERO_LEFT_ENCODED = {(byte) 0X01, (byte) 0x00};

    private static final byte[] EMPTY_STRING_BYTES = ("").getBytes();
    private static final byte[] D_BYTES = ("D").getBytes();
    private static final byte[] T_BYTES = ("T").getBytes();
    private static final byte[] N_BYTES = ("N").getBytes();
    private static final byte[] S_BYTES = ("S").getBytes();
    private static final byte[] SKE_BYTES = ("SKE").getBytes();
    private static final byte[] SKA_BYTES = ("SKA").getBytes();
    private static final byte[] SK_BYTES = ("SK").getBytes();
    private static final byte[] PK_BYTES = ("PK").getBytes();
    private static final byte[] PKE_BYTES = ("PKE").getBytes();
    private static final byte[] PKA_BYTES = ("PKA").getBytes();
    private static final int FIVE_TWELVE = 512;
    private static final int TEN_TWENTYFOUR = 1024;
    private static final int REQUIRED_BYTE_MULTIPLE = 8;
    private static final BigInteger FOUR = BigInteger.valueOf(4);
    private static final BigInteger r = BigInteger.TWO.pow(446).subtract(new BigInteger(
            "13818066809895115352007386748515426880336692474882178609894547503885"
    ));

    private static final int ROUNDS = 24;

    private static final long[] KECCAKF_ROUND_COUNT = {
            0x0000000000000001L, 0x0000000000008082L, 0x800000000000808aL,
            0x8000000080008000L, 0x000000000000808bL, 0x0000000080000001L,
            0x8000000080008081L, 0x8000000000008009L, 0x000000000000008aL,
            0x0000000000000088L, 0x0000000080008009L, 0x000000008000000aL,
            0x000000008000808bL, 0x800000000000008bL, 0x8000000000008089L,
            0x8000000000008003L, 0x8000000000008002L, 0x8000000000000080L,
            0x000000000000800aL, 0x800000008000000aL, 0x8000000080008081L,
            0x8000000000008080L, 0x0000000080000001L, 0x8000000080008008L
    };

    private static final int[] KECCAKF_ROTATION_COUNT = {
            1, 3, 6, 10, 15, 21, 28, 36, 45, 55, 2, 14,
            27, 41, 56, 8, 25, 43, 62, 18, 39, 61, 20, 44
    };

    private static final int[] KECCAKF_PI_LENGTH = {
            10, 7, 11, 17, 18, 3, 5, 16, 8, 21, 24, 4,
            15, 23, 19, 13, 12, 2, 20, 14, 22, 9, 6, 1
    };

    private static final SecureRandom myRandom = new SecureRandom();
    private static final Point G = Point.fromLeastSignificantBit(BigInteger.valueOf(8));

    Sha_3() {

    }

    public static String hash(File theFile) {
        try {
            return hash(new String(Files.readAllBytes(Paths.get(theFile.getPath()))));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String hash(String theString) {
        byte[] thePaddedString = padBytes(theString.getBytes());
        return new String(KMACXOF256(EMPTY_STRING_BYTES, thePaddedString, FIVE_TWELVE, D_BYTES));
    }

    public static String authenticationTag(File theFile, String pw) {
        try {
            return authenticationTag(new String(Files.readAllBytes(Paths.get(theFile.getPath()))), pw);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String authenticationTag(String theString, String pw) {
        byte[] thePaddedString = padBytes(theString.getBytes());
        return new String(KMACXOF256(pw.getBytes(), thePaddedString, FIVE_TWELVE, T_BYTES));
    }

    public static SymmetricCryptogram encrypt(File theFile, String pw) {
        try {
            return encrypt(new String(Files.readAllBytes(Paths.get(theFile.getPath()))), pw);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static SymmetricCryptogram encrypt(String theString, String pw) {
        byte[] z = new byte[FIVE_TWELVE];
        myRandom.nextBytes(z);

        byte[] thePaddedString = padBytes(theString.getBytes());

        byte[] keka = KMACXOF256(append(z, pw.getBytes()), EMPTY_STRING_BYTES, TEN_TWENTYFOUR, S_BYTES);
        byte[] ke = Arrays.copyOfRange(keka, 0, keka.length / 2);
        byte[] ka = Arrays.copyOfRange(keka, keka.length / 2, keka.length);
        byte[] c = (new BigInteger(KMACXOF256(ke, EMPTY_STRING_BYTES, thePaddedString.length, SKE_BYTES)).xor(new BigInteger(thePaddedString))).toByteArray();
        byte[] t = KMACXOF256(ka, thePaddedString, FIVE_TWELVE, SKA_BYTES);
        return new SymmetricCryptogram(z, c, t);
    }

    public static KeyPair dhiesKeyPair(String pw) {
        BigInteger s = new BigInteger(KMACXOF256(pw.getBytes(), EMPTY_STRING_BYTES, FIVE_TWELVE, SK_BYTES)).multiply(FOUR); // s <- KMACXOF256(pw, “”, 512, “SK”); s <- 4s
        Point V = G.scalarMultiply(s); // V <- s*G
        return new KeyPair(s, V); // (s, V)
    }

    public static SymmetricCryptogram dhiesEncrypt(File theFile, Point V) {
        try {
            return dhiesEncrypt(new String(Files.readAllBytes(Paths.get(theFile.getPath()))), V);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static SymmetricCryptogram dhiesEncrypt(String theString, Point V) {
        byte[] m = theString.getBytes();
        BigInteger k = BigInteger.valueOf(myRandom.nextInt(FIVE_TWELVE)).multiply(FOUR); // k <- Random(512); k <- 4k
        Point W = V.scalarMultiply(k); // W <- k*V;
        Point Z = G.scalarMultiply(k); // Z <- k*G
        byte[] kake = KMACXOF256(W.getX().toByteArray(), EMPTY_STRING_BYTES, TEN_TWENTYFOUR, PK_BYTES); // (ka || ke) <- KMACXOF256(Wx, “”, 1024, “PK”)
        byte[] ka = Arrays.copyOfRange(kake, 0, kake.length / 2);
        byte[] ke = Arrays.copyOfRange(kake, kake.length / 2, kake.length);
        byte[] c = (new BigInteger(KMACXOF256(ke, EMPTY_STRING_BYTES, m.length, PKE_BYTES))).xor(new BigInteger(m)).toByteArray(); // c <- KMACXOF256(ke, “”, |m|, “PKE”) xor m
        byte[] t = KMACXOF256(ka, m, FIVE_TWELVE, PKA_BYTES); // t <- KMACXOF256(ka, m, 512, “PKA”)
        return new SymmetricCryptogram(Z, c, t); // (Z, c, t)
    }

    public static String dhiesDecrypt(SymmetricCryptogram theCryptogram, String pw) {
        BigInteger s = new BigInteger(KMACXOF256(pw.getBytes(), EMPTY_STRING_BYTES, FIVE_TWELVE, SK_BYTES)).multiply(FOUR); // s <- KMACXOF256(pw, “”, 512, “SK”); s <- 4s
        Point W = theCryptogram.getZPoint().scalarMultiply(s); // W <- s*Z
        byte[] kake = KMACXOF256(W.getX().toByteArray(), EMPTY_STRING_BYTES, TEN_TWENTYFOUR, PK_BYTES); // (ka || ke) <- KMACXOF256(Wx, “”, 1024, “PK”)
        byte[] ka = Arrays.copyOfRange(kake, 0, kake.length / 2);
        byte[] ke = Arrays.copyOfRange(kake, kake.length / 2, kake.length);
        byte[] m = (new BigInteger(KMACXOF256(ke, EMPTY_STRING_BYTES, theCryptogram.getC().length, PKE_BYTES))).xor(new BigInteger(theCryptogram.getC())).toByteArray(); // m <- KMACXOF256(ke, “”, |c|, “PKE”) xor c
        byte[] t = KMACXOF256(ka, m, FIVE_TWELVE, PKA_BYTES); // t’ <- KMACXOF256(ka, m, 512, “PKA”)
        return Arrays.equals(t, theCryptogram.getT()) ? new String(m).replaceAll("\0", "") : "UNACCEPTED"; // accept if, and only if, t’ = t
    }

    public static Signature dhiesSign(File theFile, String pw) {
        try {
            return dhiesSign(new String(Files.readAllBytes(Paths.get(theFile.getPath()))), pw);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Signature dhiesSign(String theString, String pw) {
        byte[] m = theString.getBytes();
        BigInteger s = new BigInteger(KMACXOF256(pw.getBytes(), EMPTY_STRING_BYTES, FIVE_TWELVE, SK_BYTES)).multiply(FOUR); // s <- KMACXOF256(pw, “”, 512, “SK”); s <- 4s
        BigInteger k = new BigInteger(KMACXOF256(s.toByteArray(), m, FIVE_TWELVE, N_BYTES)).multiply(FOUR); // k <- KMACXOF256(s, m, 512, “N”); k <- 4k
        Point U = G.scalarMultiply(k); // U <- k*G
        BigInteger h = new BigInteger(KMACXOF256(U.getX().toByteArray(), m, FIVE_TWELVE, T_BYTES)); // h <- KMACXOF256(Ux, m, 512, “T”)
        BigInteger z = k.subtract(h.multiply(s)).mod(r); // z  (k – hs) mod r
        return new Signature(h, z); // (h, z)
    }

    public static boolean dhiesVerify(Signature theSignature, File theFile, Point V) {
        try {
            return dhiesVerify(theSignature, new String(Files.readAllBytes(Paths.get(theFile.getPath()))), V);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean dhiesVerify(Signature theSignature, String theString, Point V) {
        byte[] m = theString.getBytes();
        Point U = G.scalarMultiply(theSignature.getZ()).add(V.scalarMultiply(theSignature.getH()));
        return new BigInteger(KMACXOF256(U.getX().toByteArray(), m, FIVE_TWELVE, T_BYTES)).equals(theSignature.getH());
    }

    public static String decrypt(SymmetricCryptogram theCryptogram, String pw) {
        byte[] keka = KMACXOF256(append(theCryptogram.getZ(), pw.getBytes()), EMPTY_STRING_BYTES, TEN_TWENTYFOUR, S_BYTES);
        byte[] ke = Arrays.copyOfRange(keka, 0, keka.length / 2);
        byte[] ka = Arrays.copyOfRange(keka, keka.length / 2, keka.length);
        byte[] m = (new BigInteger(KMACXOF256(ke, EMPTY_STRING_BYTES, theCryptogram.getC().length, SKE_BYTES)).xor(new BigInteger(theCryptogram.getC()))).toByteArray();
        byte[] t = KMACXOF256(ka, m, FIVE_TWELVE, SKA_BYTES);
        return Arrays.equals(t, theCryptogram.getT()) ? new String(m).replaceAll("\0", "") : "UNACCEPTED";
    }

    private static byte[] cSHAKE256(byte[] X, int L, byte[] N, byte[] S) {
        if ((L & 7) != 0) {
            throw new IllegalArgumentException("SHAKE mandates that the output length must be a multiple of 8");
        }

        byte[] outputBytes = new byte[L >>> 3];
        SHAKE shake = new SHAKE();
        shake.customize(N, S);
        shake.absorb(X, X.length);
        shake.changeMode();
        shake.squeeze(outputBytes, L >>> 3);
        return outputBytes;
    }

    private static byte[] KMACXOF256(byte[] K, byte[] X, int L, byte[] S) {
        byte[] newX = append(append(bytepad(encodeString(K), 136), X), rightEncode(L));
        return cSHAKE256(newX, L, SHAKE.KMAC_IN_BYTES, S);
    }

    public static byte[] bytepad(byte[] X, int w) {
        byte[] encoding = leftEncode(w);
        byte[] z = new byte[w * ((encoding.length + X.length + w - 1) / w)]; // as per the sha3 documentation
        System.arraycopy(encoding, 0, z, 0, encoding.length);
        System.arraycopy(X, 0, z, encoding.length, X.length);
        for (int i = encoding.length; i < z.length; i++) {
            z[i] = (byte) 0;
        }

        return z;
    }

    public static byte[] encodeString(byte[] theString) {
        int theStringLength = (theString == null) ? 0 : theString.length;

        byte[] lengthEncoding = (theString == null) ? ZERO_LEFT_ENCODED : leftEncode(theStringLength << 3);
        byte[] theStringEncoding = new byte[lengthEncoding.length + theStringLength];
        System.arraycopy(lengthEncoding, 0, theStringEncoding, 0, lengthEncoding.length);
        System.arraycopy((theString == null) ? theStringEncoding : theString, 0, theStringEncoding, lengthEncoding.length, theStringLength);
        return theStringEncoding;
    }

    private static byte[] leftEncode(int x) {
        int n = 1;
        while ((1 << (8 * n)) <= x) {
            n++;
        }

        byte[] xEncoding = new byte[n + 1];
        for (int i = n; i > 0; i--) {
            xEncoding[i] = (byte) (x & 0xFF);
            x >>>= 8;
        }

        xEncoding[0] = (byte) n;
        return xEncoding;
    }

    private static byte[] rightEncode(int x) {
        int n = 1;
        while ((1 << (8 * n)) <= x) {
            n++;
        }

        byte[] xEncoding = new byte[n + 1];
        for (int i = 0; i < n - 1; i++) {
            xEncoding[i] = (byte) (x & 0xFF);
            x >>>= 8;
        }
        xEncoding[n] = (byte) n;
        return xEncoding;
    }

    private static long rotateLeft(long theValue, int thePositions) {
        return (theValue << thePositions) | (theValue >> (64 - thePositions));
    }

    /**
     * The Keccak function inspired from Markku-Juhani Saarinen's C implementation:
     * https://github.com/mjosaarinen/tiny_sha3/blob/master/sha3.c
     *
     * @param theState the state to hash, must be a byte array of length 200
     * @return the newly hashed up state
     */
    public static byte[] KECCAKF(byte[] theState) {
        long[] stateLongMapping = new long[25];
        long[] bc = new long[5];

        for (int i = 0, j = 0; i < 25; i++, j += 8) {
            stateLongMapping[i] = (((long) theState[j] & 0xFFL)) | (((long) theState[j + 1] & 0xFFL) << 8) |
                    (((long) theState[j + 2] & 0xFFL) << 16) | (((long) theState[j + 3] & 0xFFL) << 24) |
                    (((long) theState[j + 4] & 0xFFL) << 32) | (((long) theState[j + 5] & 0xFFL) << 40) |
                    (((long) theState[j + 6] & 0xFFL) << 48) | (((long) theState[j + 7] & 0xFFL) << 56);
        }

        for (int round = 0; round < ROUNDS; round++) {
            for (int i = 0; i < 5; i++) {
                bc[i] = stateLongMapping[i] ^ stateLongMapping[i + 5] ^ stateLongMapping[i + 10] ^ stateLongMapping[i + 15] ^ stateLongMapping[i + 20];
            }

            for (int i = 0; i < 5; i++) {
                long t = bc[(i + 4) % 5] ^ rotateLeft(bc[(i + 1) % 5], 1);
                for (int j = 0; j < 25; j += 5) {
                    stateLongMapping[j + i] ^= t;
                }
            }

            long t = stateLongMapping[1];

            for (int i = 0; i < 24; i++) {
                int j = KECCAKF_PI_LENGTH[i];
                bc[0] = stateLongMapping[j];
                stateLongMapping[j] = rotateLeft(t, KECCAKF_ROTATION_COUNT[i]);
                t = bc[0];
            }

            for (int i = 0; i < 25; i += 5) {
                for (int j = 0; j < 5; j++) {
                    bc[j] = stateLongMapping[i + j];
                }
                for (int j = 0; j < 5; j++) {
                    stateLongMapping[i + j] ^= (~bc[(j + 1) % 5]) & bc[(j + 2) % 5];
                }
            }

            stateLongMapping[0] ^= KECCAKF_ROUND_COUNT[round];
        }

        for (int i = 0, j = 0; i < 25; i++, j += 8) {
            long longToConvert = stateLongMapping[i];
            theState[j + 0] = (byte) ((longToConvert) & 0xFF);
            theState[j + 1] = (byte) ((longToConvert >> 8) & 0xFF);
            theState[j + 2] = (byte) ((longToConvert >> 16) & 0xFF);
            theState[j + 3] = (byte) ((longToConvert >> 24) & 0xFF);
            theState[j + 4] = (byte) ((longToConvert >> 32) & 0xFF);
            theState[j + 5] = (byte) ((longToConvert >> 40) & 0xFF);
            theState[j + 6] = (byte) ((longToConvert >> 48) & 0xFF);
            theState[j + 7] = (byte) ((longToConvert >> 56) & 0xFF);
        }

        return theState;
    }

    public static byte[] append(byte[] theHost, byte[] theBytesToAppend) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(theHost);
            baos.write(theBytesToAppend);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] append(byte[] theHost, byte theByteToAppend) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(theHost);
            baos.write(theByteToAppend);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] padBytes(byte[] theBytes) {
        while (theBytes.length % REQUIRED_BYTE_MULTIPLE != 0) {
            theBytes = append(theBytes, (byte) 0);
        }
        return theBytes;
    }
}
