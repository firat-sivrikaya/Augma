package world.augma.asset;

/**
 * Custom 2D Vector component for the implementation of Circle Packing Algorithm.
 *
 * Created by Burak on 30-Apr-18.
 */
public class CPAVector {

    public static class Cartesian {

        /* Fields */
        private double x;
        private double y;

        /* Constructors */
        public Cartesian() {
            this(0, 0);
        }

        public Cartesian(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public Cartesian(double x1, double y1, double x2, double y2) {
            this.x = x2 - x1;
            this.y = y2 - y1;
        }

        /**
         * @return sqrt(x^2, y^2): length of this vector.
         */
        public double length() {
            return Math.hypot(x, y);
        }

        /**
         * Adds this CPAVector.Cartesian and the vector passed as argument.
         *
         * @param vector to be added.
         * @return Summation vector.
         */
        public void add(Cartesian vector) {
            this.x += vector.x;
            this.y += vector.y;
        }

        /**
         * Subtracts vector passed as argument from this CPAVector.Cartesian instance.
         *
         * @param vector to be subtracted.
         * @return Resulting subtraction vector.
         */
        public void subtract(Cartesian vector) {
           this.x -= vector.x;
           this.y -= vector.y;
        }

        /**
         * @return the unit vector normalization of this instance.
         */
        public void normalize() {
            double len = Math.hypot(x, y);

            this.x /= len;
            this.y /= len;
        }

        /**
         * Multiplies this CPAVector.Cartesian by a scalar constant.
         *
         * @param factor of scaling.
         * @return resulting vector after scaling by factor.
         */
        public void scale(double factor) {
            this.x *= factor;
            this.y *= factor;
        }

        public void resize(double length) {
            Polar polar = convertToPolar();
            polar.resize(length);
            polar.convertToCartesian();
        }

        /**
         * @return x component
         */
        public double getX() {
            return x;
        }

        /**
         * Sets x component of this CPAVector.Cartesian instance
         *
         * @param x value to be set
         */
        public void setX(double x) {
            this.x = x;
        }

        /**
         * @return y component
         */
        public double getY() {
            return y;
        }

        /**
         * Sets y component of this CPAVector.Cartesian instance
         *
         * @param y value to be set
         */
        public void setY(double y) {
            this.y = y;
        }

        /**
         * Converts this CPAVector.Cartesian instance
         * to its corresponding CPAVector.Polar instance.
         *
         * @return CPAVector.Polar version of this CPAVector.Cartesian
         * instance.
         */
        public Polar convertToPolar() {
            return new Polar(Math.hypot(x, y), Math.atan2(y, x));
        }
    }

    public static class Polar {

        /**
         * Fields.
         *
         * Angle is in radians.
         */
        private double radius;
        private double theta;

        /* Constructors */
        public Polar() {
            this(0, 0);
        }

        public Polar(double radius, double theta) {
            this.radius = radius;
            this.theta = theta;
        }

        /**
         * @return length of this vector.
         */
        public double length() {
            return radius;
        }

        /**
         * Adds this CPAVector.Polar and the vector passed as argument.
         *
         * @param vector to be added.
         * @return Summation vector.
         */
        public void add(Polar vector) {
            Cartesian c1 = convertToCartesian();
            Cartesian c2 = vector.convertToCartesian();

            c1.add(c2);
            c1.convertToPolar();
        }

        /**
         * Subtracts vector passed as argument from this CPAVector.Polar instance.
         *
         * @param vector to be subtracted.
         * @return Resulting subtraction vector.
         */
        public void subtract(Polar vector) {
            Cartesian c1 = convertToCartesian();
            Cartesian c2 = vector.convertToCartesian();

            c1.subtract(c2);
            c1.convertToPolar();
        }

        /**
         * @return the unit vector normalization of this instance.
         */
        public void normalize() {
            radius = 1;
        }

        /**
         * Multiplies this CPAVector.Polar by a scalar constant.
         *
         * @param factor of scaling.
         * @return resulting vector after scaling by factor.
         */
        public void scale(double factor) {
            radius *= factor;
        }

        public void resize(double length) {
            radius = length;
        }

        /**
         * @return radius component
         */
        public double getRadius() {
            return radius;
        }

        /**
         * Sets radius component of this CPAVector.Polar instance
         *
         * @param radius value to be set
         */
        public void setRadius(double radius) {
            this.radius = radius;
        }

        /**
         * @return theta component
         */
        public double getTheta() {
            return theta;
        }

        /**
         * Sets theta component of this CPAVector.Polar instance
         *
         * @param theta value to be set
         */
        public void setTheta(double theta) {
            this.theta = theta;
        }

        /**
         * Converts this CPAVector.Polar instance
         * to its corresponding CPAVector.Cartesian instance.
         *
         * @return CPAVector.Cartesian version of this CPAVector.Polar
         * instance.
         */
        public Cartesian convertToCartesian() {
            return new Cartesian(radius * Math.cos(theta), radius * Math.sin(theta));
        }
    }
}
