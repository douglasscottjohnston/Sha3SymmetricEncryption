import java.math.BigInteger;

public final class Point {
    public static final BigInteger p = BigInteger.TWO.pow(448).subtract(BigInteger.TWO.pow(224)).subtract(BigInteger.ONE);
    private static final BigInteger d = BigInteger.valueOf(-39081);
    private static final Point NEUTRAL_ELEMENT_OF_ADDITION = new Point(BigInteger.ZERO, BigInteger.ONE);
    private final BigInteger myX;
    private final BigInteger myY;

    Point(BigInteger x, BigInteger y) {
        myX = x;
        myY = y;
    }

    @Override
    public String toString() {
        return myX.toString() + " " + myY.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Point) {
            return (o == this) || myX.equals(((Point) o).getX()) && myY.equals(((Point) o).getY());
        }
        return false;
    }

    /**
     * Uses the Edwards point addition formula to add two points together and returns the resulting point
     *
     * @param p2 The second point to add
     * @return The result of the addition
     */
    public Point add(Point p2) {
        if (p2.equals(this.negative())) return NEUTRAL_ELEMENT_OF_ADDITION;
        if (p2.equals(NEUTRAL_ELEMENT_OF_ADDITION)) return this;
        if (this.equals(NEUTRAL_ELEMENT_OF_ADDITION)) return p2;

        // p1 + p2 =
        return new Point(
                // ((p1.x * p2.x + p1.y * p2.y) / (1 + d * p1.x * p2.x * p1.y * p2.y)

                myX.multiply(p2.getY()).mod(p).add(myY.multiply(p2.getY())).mod(p).multiply(
                        BigInteger.ONE.add(d.multiply(myX).multiply(p2.getX()).multiply(myY).multiply(p2.getY())).modInverse(p)
                ).mod(p),
                // (p1.y * p2.y - p1.x * p2.y) / (1 - d * p1.x * p2.x * p1.y * p2.y)
                myY.multiply(p2.getY()).mod(p).subtract(myX.multiply(p2.getX())).mod(p).multiply(
                        BigInteger.ONE.subtract(d.multiply(myX).multiply(p2.getX()).multiply(myY).multiply(p2.getY())).modInverse(p)
                ).mod(p)
        );
    }

    /**
     * Preforms scalar multiplication of the point by scalar s
     * using the exponentiation algorithm
     *
     * @param s The scalar
     * @return The result of the exponentiation algorithm
     */
    public Point scalarMultiply(BigInteger s) {
        if (s.signum() == 0) return NEUTRAL_ELEMENT_OF_ADDITION;
        if (s.equals(BigInteger.ONE)) return this;
        Point G = this;
        if (s.signum() == -1) {
            s = s.negate();
            G = this.opposite();
        }
        Point P = G;
        byte[] sBytes = s.toByteArray();
        int k = sBytes.length;
        for (int i = k - 1; i >= 0; i--) {
            P = P.add(P);
            if (s.testBit(i)) {
                P = P.add(G);
            }
        }
        return P; // P = s * G
    }

    /**
     * Given an x, returns a point (x, y) where y is the result of the equation:
     * y = sqrt((1-x^2)/(1+39081x^2)mod p)
     *
     * @param theX the x to use in the point
     * @return The resulting point
     */
    public static Point fromLeastSignificantBit(BigInteger theX) {
        return new Point(
                theX,
                // sqrt( (1 - x^2) / (1 + 39081 * x^2) mod p)
                sqrt(
                        // (1 - x^2)
                        BigInteger.ONE.subtract(theX.pow(2).mod(p)).mod(p).multiply(
                                // (1 + 39081 * x^2) mod p
                                BigInteger.ONE.add(BigInteger.valueOf(39081).multiply(theX.pow(2).mod(p)).mod(p)).mod(p).modInverse(p)
                        ),
                        Point.p, theX.getLowestSetBit() != 0
                ));
    }

    /**
     * Compute a square root of v mod p with a specified
     * the least significant bit, if such a root exists.
     *
     * @param v   the radicand.
     * @param p   the modulus (must satisfy p mod 4 = 3).
     * @param lsb desired least significant bit (true: 1, false: 0).
     * @return a square root r of v mod p with r mod 2 = 1 iff lsb = true
     * if such a root exists, otherwise null.
     */
    private static BigInteger sqrt(BigInteger v, BigInteger p, boolean lsb) {
        assert (p.testBit(0) && p.testBit(1)); // p = 3 (mod 4)
        if (v.signum() == 0) {
            return BigInteger.ZERO;
        }
        BigInteger r = v.modPow(p.shiftRight(2).add(BigInteger.ONE), p);
        if (r.testBit(0) != lsb) {
            r = p.subtract(r); // correct the lsb
        }
        return (r.multiply(r).subtract(v).mod(p).signum() == 0) ? r : null;
    }

    /**
     * Given point (x, y)
     *
     * @return The opposite of the point (-x, y)
     */
    public Point opposite() {
        return new Point(myX.negate(), myY);
    }

    /**
     * Given point (x, y) and modulus p
     *
     * @return The negation of the point (p - x, y)
     */
    public Point negative() {
        return new Point(p.subtract(myX), myY);
    }

    public BigInteger getX() {
        return myX;
    }

    public BigInteger getY() {
        return myY;
    }
}