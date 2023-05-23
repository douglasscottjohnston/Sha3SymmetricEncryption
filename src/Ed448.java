import java.math.BigInteger;


public class Ed448 {
    private static final BigInteger d = BigInteger.valueOf(-39081);
    Point neutralElementOfAddition = new Point(BigInteger.ZERO, BigInteger.ONE);

    Ed448() {

    }

    public BigInteger computeCurve(Point p) {
        return BigInteger.ONE.add(d.multiply(p.getX().pow(2)).multiply(p.getY().pow(2)));
    }

    class Point {
        private BigInteger myX;
        private BigInteger myY;

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
            // p1 + p2 =
            return new Point(
                    // ((p1.x * p2.x + p1.y * p2.y) / (1 + d * p1.x * p2.x * p1.y * p2.y)

                    myX.multiply(p2.getY()).add(myY.multiply(p2.getY())).divide(
                            BigInteger.ONE.add(d.multiply(myX).multiply(p2.getX()).multiply(myY).multiply(p2.getY()))
                    ),
                    // (p1.y * p2.y - p1.x * p2.y) / (1 - d * p1.x * p2.x * p1.y * p2.y)
                    myY.multiply(p2.getY()).subtract(myX.multiply(p2.getX())).divide(
                            BigInteger.ONE.subtract(d.multiply(myX).multiply(p2.getX()).multiply(myY).multiply(p2.getY()))
                    )
            );
        }

        public Point opposite() {
            return new Point(myX.negate(), myY);
        }

        public BigInteger getX() {
            return myX;
        }

        public void setX(final BigInteger myX) {
            this.myX = myX;
        }

        public BigInteger getY() {
            return myY;
        }

        public void setY(final BigInteger myY) {
            this.myY = myY;
        }
    }
}
