import java.io.IOException;
import java.util.Arrays;

public class SHAKE {

    public static final byte[] KMAC_IN_BYTES = ("KMAC").getBytes();

    private static final int r = 136; // 8 bits per byte * 136 = 1088 bits for r, as determined by Keccack
    private byte[] myState = new byte[200]; // 8 bits per byte * 200 = 1600 bits for the state, as determined by Keccack

    private Sha_3 mySha;
    private int myCount = 0;
    private boolean isMacKey;
    private boolean isCShake;

    SHAKE() {
        mySha = new Sha_3();
    }

    public void customize(byte[] N, byte[] S) {
        if ((N == null || N.length == 0) && (S == null || S.length == 0))
            return;

        isMacKey = Arrays.equals(N, KMAC_IN_BYTES);

        byte[] customization = mySha.bytepad(mySha.append(mySha.encodeString(N), mySha.encodeString(S)), 136);

        absorb(customization, customization.length);

        if (isMacKey) {
            isCShake = true;
            byte[] macKeyCustomization = Sha_3.bytepad(Sha_3.encodeString(N), 136);
            absorb(macKeyCustomization, macKeyCustomization.length);
        }
    }

    public void absorb(byte[] theData, int theLength) {
        int j = myCount;
        for (int i = 0; i < theLength; i++) {
            myState[j++] ^= theData[i];
            if (j >= r) {
                myState = mySha.KECCAKF(myState);
                j = 0;
            }
        }
    }

    public void changeMode() {
        if (isMacKey) {
            absorb(mySha.ZERO_RIGHT_ENCODED, mySha.ZERO_RIGHT_ENCODED.length);
        }

        myState[myCount] ^= (byte) (isCShake ? 0x04 : 0x1F);
        myState[r - 1] ^= (byte) 0x80;
        myState = mySha.KECCAKF(myState);
        myCount = 0;
    }

    public void squeeze(byte[] theData, int theLength) {
        int j = myCount;
        for (int i = 0; i < theLength; i++) {
            if (j >= r) {
                myState = mySha.KECCAKF(myState);
                j = 0;
            }
            theData[i] = myState[j++];
        }
        myCount = j;
    }
}