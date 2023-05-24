import java.math.BigInteger;


public class Ed448 {
    private static final BigInteger d = BigInteger.valueOf(-39081);
    private static final BigInteger MODULUS = BigInteger.valueOf(4);
    private final Point neutralElementOfAddition = new Point(BigInteger.ZERO, BigInteger.ONE);

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
                return (o == this) || this.myX == ((Point) o).getX() && this.myY == ((Point) o).getY();
            }
            return false;
        }

        public Point add(Point p2) {
            //TODO: Implement modular arithmatic

            // p1 + p2 =
            return new Point(
                    // ((p1.x * p2.x + p1.y * p2.y) / (1 + d * p1.x * p2.x * p1.y * p2.y)

                    myX.multiply(p2.getY()).mod(MODULUS).add(myY.multiply(p2.getY())).mod(MODULUS).multiply(
                            BigInteger.ONE.add(d.multiply(myX).multiply(p2.getX()).multiply(myY).multiply(p2.getY())).modInverse(MODULUS)
                    ).mod(MODULUS),
                    // (p1.y * p2.y - p1.x * p2.y) / (1 - d * p1.x * p2.x * p1.y * p2.y)
                    myY.multiply(p2.getY()).mod(MODULUS).subtract(myX.multiply(p2.getX())).mod(MODULUS).multiply(
                            BigInteger.ONE.subtract(d.multiply(myX).multiply(p2.getX()).multiply(myY).multiply(p2.getY())).modInverse(MODULUS)
                    ).mod(MODULUS)
            );
        }

        public Point fromLeastSignificantYBit() {
            return new Point(this.myX, s)
        }

        /**
         * Preforms scalar multiplication of the point by scalar s
         * using the exponentiation algorithm
         *
         * @param s The scalar
         * @return The result of the exponentiation algorithm
         */
        public Point scalarMultiply(BigInteger s) {
            if (s.signum() == 0) return new Point(BigInteger.ZERO, BigInteger.ZERO);
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
                if(s.testBit(i)) {
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

        public BigInteger getX() {
            return myX;
        }

        public BigInteger getY() {
            return myY;
        }

        /**
         * Compute a square root of v mod p with a specified
         * least significant bit, if such a root exists.
         *
         * @param v the radicand.
         * @param p the modulus (must satisfy p mod 4 = 3).
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
