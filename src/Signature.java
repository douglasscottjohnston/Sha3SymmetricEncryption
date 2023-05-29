import java.math.BigInteger;

public final class Signature {
    private final BigInteger myH;
    private final BigInteger myZ;

    Signature(BigInteger h, BigInteger z) {
        myH = h;
        myZ = z;
    }

    @Override
    public String toString() {
        return myH.toString() + " " + myZ.toString();
    }

    public BigInteger getH() {
        return myH;
    }

    public BigInteger getZ() {
        return myZ;
    }
}
