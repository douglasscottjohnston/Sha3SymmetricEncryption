public class SymmetricCryptogram {

    private byte[] myZ;
    private byte[] myC;
    private byte[] myT;

    SymmetricCryptogram(byte[] z, byte[] c, byte[] t) {
        myZ = z;
        myC = c;
        myT = t;
    }

    @Override
    public String toString() {
        return new String(getT());
    }

    public byte[] getZ() {
        return myZ;
    }

    public byte[] getC() {
        return myC;
    }

    public byte[] getT() {
        return myT;
    }
}