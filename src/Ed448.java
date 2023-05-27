import java.math.BigInteger;


public class Ed448 {
    private static final BigInteger p = BigInteger.TWO.pow(448).subtract(BigInteger.TWO.pow(224)).subtract(BigInteger.ONE);
    private static final BigInteger d = BigInteger.valueOf(-39081);
    private static final BigInteger negative_d = BigInteger.valueOf(39081);
//    private final Point G = new Point(BigInteger.valueOf(8), );
    private final Point NEUTRAL_ELEMENT_OF_ADDITION = new Point(BigInteger.ZERO, BigInteger.ONE);

    Ed448() {

    }

    public BigInteger computeCurve(Point p) {
        return BigInteger.ONE.add(d.multiply(p.getX().pow(2)).multiply(p.getY().pow(2)));
    }

    final class Point {
        private final BigInteger myX;
        private final BigInteger myY;

        Point(BigInteger x, BigInteger y) {
            myX = x;
            myY = y;
        }


        @Override
        public boolean equals(Object o) {
            if (o instanceof Point) {
                return (o == this) || myX == ((Point) o).getX() && myY == ((Point) o).getY();
            }
            return false;
        }

        public Point add(Point p2) {
            if (p2.equals(this.opposite())) return NEUTRAL_ELEMENT_OF_ADDITION;

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

        public Point fromLeastSignificantYBit() {
            // TODO: idk about this
            return new Point(
                    myX,
                    // sqrt( (1 - x^2) / (1 + 39081 * x^2) mod p)
                    sqrt(
                            BigInteger.ONE.subtract(myX.multiply(BigInteger.TWO).mod(p)).mod(p).multiply(
                                    BigInteger.ONE.add(
                                            negative_d.multiply(myX.pow(2).mod(p)).mod(p)
                                    ).modInverse(p)
                            ),
                            p, myY.getLowestSetBit() == 0 ? false : true
                    ));
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
    }
}
