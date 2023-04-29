import java.io.File;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.BitSet;

public class Sha_3 {

    private static final int SIZE = 16; // The number of bytes in 256 bits
    private static final BitSet ZERO = BitSet.valueOf(new long[]{0});
    private static final BitSet TWO_FIFTY_FIVE = BitSet.valueOf(new long[]{255});

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

    Sha_3() {

    }

    public static void encrypt(File file) {

    }

    public static String encrypt(String string) {
        byte[] output = encodeString(string);

        return output.toString();
    }

    public static void decrypt(File file, BitSet key) {

    }

    public static String decrypt(String string, BitSet key) {

        return string;
    }

    private static byte[] bytepad(byte[] X, int w) {
        byte[] encoding = leftEncode(w);
        byte[] z = new byte[w * ((encoding.length + X.length + w - 1) / w)]; // as per the sha3 documentation
        System.arraycopy(encoding, 0, z, 0, encoding.length);
        for (int i = en; i < ; i++) {

        }

        return z;
    }

    private static byte[] encodeString(String s) {
        byte[] output = bytepad(s.getBytes(), s.length());


        return output;
    }

    private static byte[] leftEncode(int x) {
        byte[] bytes = ByteBuffer.allocate(SIZE).putInt(x).array();
        bytes = reverse(bytes);
        bytes[0] = 1;
        print(bytes);
        System.out.println(bytes[3]);
        return bytes;
    }

    private static byte[] rightEncode(int x) {
        byte[] bytes = ByteBuffer.allocate(SIZE).putInt(x).array();
        bytes = reverse(bytes);
        bytes[SIZE - 1] = 1;
        print(bytes);


        return bytes;
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

    private static byte[] reverse(byte[] bits) {
        byte[] reversed = new byte[bits.length];

        int j = 0;
        for (int i = bits.length - 1; i > -1; i--) {
            reversed[j] = bits[i];
            j++;
        }

        return reversed;
    }


    private static void print(byte[] bytes) {
        for (byte b : bytes) {
            System.out.print(b);
        }
        System.out.println();
    }
}
