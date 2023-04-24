import java.io.File;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.BitSet;

public class Sha_3 {

    private static final int SIZE = 16; // The number of bytes in 256 bits
    private static final BitSet ZERO = BitSet.valueOf(new long[]{0});
    private static final BitSet TWO_FIFTY_FIVE = BitSet.valueOf(new long[]{255});

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

    private static byte[] KMACXOF256(BitSet K, BitSet X, int L, BitSet S) {

        return encodeString("hi");
    }

    private static byte[] bytepad(byte[] X, int w) {
        byte[] z = leftEncode(w);

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

    private static byte[] cSHAKE256(String X, int L, String N, String S) {
        if(N.isBlank() && S.isBlank()) {
            return SHAKE(X, L);
        }
        return KECCAK
    }

    private static byte[] SHAKE(String X, int L) {

    }

    private static byte[] KECCAK() {

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
