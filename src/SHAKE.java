import java.util.Arrays;

public class SHAKE {

    private static final int BLOCK_SIZE = 256;
    private static final int ROUNDS = 24;
    private static final int r = 136; // 8 bits per byte * 136 = 1088 bits for r, as determined by Keccack
    private static final int c = 64; // 8 bits per byte * 64 = 512 bits for c, as determined by Keccack
    private byte[] myState = new byte[200]; // 8 bits per byte * 200 = 1600 bits for the state, as determined by Keccack
    private Sha_3 mySha;
    private int myCount = 0;
    private boolean myAbsorbMode;

    SHAKE() {
        mySha = new Sha_3();
    }

    public void absorb(byte[] theData, int theLength) {
        if (!myAbsorbMode) {
            myAbsorbMode = true;
            myCount = 0;
        }

        int j = myCount;
        for (int i = 0; i < theLength; i++) {
            myState[i] ^= theData[i];
            if (j >= r) {
                myState = mySha.KECCAKF(myState);
                j = 0;
            }
        }
    }

    public void squeeze(byte[] theData, int theLength) {
        if (myAbsorbMode) {
            myAbsorbMode = false;
            myCount = 0;
        }

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