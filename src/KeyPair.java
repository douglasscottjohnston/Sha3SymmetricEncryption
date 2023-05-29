import java.math.BigInteger;

final class KeyPair {
    private final BigInteger s;
    private final Point V;

    KeyPair(BigInteger theS, Point theV) {
        s = theS;
        V = theV;
    }

    public BigInteger getS() {
        return s;
    }

    public Point getV() {
        return V;
    }
}