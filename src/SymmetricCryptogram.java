public class SymmetricCryptogram {

    private byte[] myZ;
    private Point myZPoint;
    private final byte[] myC;
    private final byte[] myT;

    SymmetricCryptogram(byte[] z, byte[] c, byte[] t) {
        myZ = z;
        myC = c;
        myT = t;
    }

    SymmetricCryptogram(Point z, byte[] c, byte[] t) {
        myZPoint = z;
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

    public Point getZPoint() {
        return myZPoint;
    }

    public byte[] getC() {
        return myC;
    }

    public byte[] getT() {
        return myT;
    }
}