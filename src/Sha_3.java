import java.io.File;
import java.util.BitSet;

public class Sha_3 {

    private static final int SIZE = 256;
    private static final BitSet ZERO = BitSet.valueOf(new long[]{0});

    Sha_3() {

    }

    public static void encrypt(File file) {

    }

    public static String encrypt(String string) {

        return string;
    }

    public static void decrypt(File file, BitSet key) {

    }

    public static String decrypt(String string, BitSet key) {

        return string;
    }

    private static BitSet KMACXOF256(BitSet K, BitSet X, int L, BitSet S) {

        return encodeString("hi");
    }

    private static BitSet bytepad(BitSet X, int w) {
        BitSet z = leftEncode(w);

        return z;
    }

    private static BitSet encodeString(String s) {
        BitSet output = bytepad(BitSet.valueOf(s.getBytes()), s.length());



        return output;
    }

    private static BitSet leftEncode(int x) {
        BitSet output = BitSet.valueOf(new long[]{x});
        output.and(ZERO);


        return output;
    }

    private static BitSet rightEncode(int x) {
        BitSet output = BitSet.valueOf(new long[]{x});
        output.and(ZERO);


        return output;
    }
}
