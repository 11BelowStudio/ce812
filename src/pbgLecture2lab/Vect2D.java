package pbgLecture2lab;

import java.io.Serializable;

/**
 * A 2D Vector
 *
 * Based on the CE218 Vector2D class, but modified for PBG course to be IMMUTABLE
 */
public final class Vect2D implements Serializable {
	// Based on the Vector2D class, but modified for PBG course to be IMMUTABLE

	public final double x, y;

	/**
	 * Creates a null vector (0,0)
	 */
	public Vect2D() {
		this(0, 0);
	}

	/**
	 * create vector with given coordinates
	 * @param x x coordinate
	 * @param y y coordinate.
	 */
	public Vect2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Creates a new vector that's a copy of the argument vector
	 * @param v vector to copy
	 */
	public Vect2D(Vect2D v) {
		this(v.x, v.y);
	}

	/**
	 * Whether this is equal to another object.
	 * True if the other object is a Vect2D  with same x and y
	 * @param o other object
	 * @return whether or not this is equal to the other object
	 */
	public boolean equals(Object o) {
		if (o instanceof Vect2D) {
			Vect2D v = (Vect2D) o;
			return x == v.x && y == v.y;
		}
		return false;
	}

	/**
	 * The angle of this vector
	 * @return the angle of this vector
	 */
	public double angle() {
		return Math.atan2(y, x);
	}

	/**
	 * Gets angle between this vector and another vector in range [-PI,PI]
	 * @param other the other vector
	 * @return angle between this vector and another vector in range [-PI,PI]
	 */
	public double angle(Vect2D other) {
		//finding difference between the angles
		double result = other.angle() - this.angle();
		//wrapping the result around if it's outside range [-PI,PI] to keep it in range
		if (result < -Math.PI){
			result += 2*Math.PI;
			//2pi added if it's below -pi
		} else if (result > Math.PI){
			result -= 2* Math.PI;
			//2pi removed if it's above pi
		}
		return result;
	}


	public String toString() {
		return "(" + String.format("%.01f", x) + "," + String.format("%.01f", y)
				+ ")";
	}

	/**
	 * Adds a vector equal to this + v
	 * @param v vector being added to a copy of this vector
	 * @return a vector equal to this+v
	 */
	public Vect2D add(Vect2D v) {
		return new Vect2D(x + v.x, y + v.y);
	}

	/**
	 * Scaled addition of two vectors.
	 * note: vector subtraction can be expressed as scaled addition with factor (-1)
	 * @param v the other vector
	 * @param fac how much that other vector will be scaled by
	 * @return a vector equal to this + (v * fac)
	 */
	public Vect2D addScaled(Vect2D v, double fac) {
		return new Vect2D(x + (v.x * fac), y + (v.y * fac));
	}

	/**
	 * Returns a new vector equal to this*fac
	 * @param fac how much to multiply this vector by
	 * @return result of this * fac
	 */
	public Vect2D mult(double fac) {
		return new Vect2D(this.x * fac,this.y * fac);
	}

	/**
	 * Adds a vector equal to v1 - v2
	 * @param v1 the initial vector
	 * @param v2 the vector being subtracted
	 * @return a vector equal to v1 - v2
	 */
	public static Vect2D minus(Vect2D v1, Vect2D v2) {
		// returns v1-v2
		return v1.addScaled(v2, -1);
	}

	/**
	 * Dot product of this vector and another one
	 * @param v other vector
	 * @return this dot v
	 */
	public double scalarProduct(Vect2D v) {
		return (x * v.x) + (y * v.y);
	}


	/**
	 * https://stackoverflow.com/a/3838398
	 * @param line the vector that's being used as the line
	 * @param point the point that we're seeing is above or below that line
	 * @return a scalar version of the cross product of this vector and the other vector
	 */
	public static double CROSS_PRODUCT(Vect2D line, Vect2D point){
		return (line.x*point.y) - (line.y * point.x);
	}

	/**
	 * Normalization
	 * @return normalized version of this vector. if this vector is 0,0, returns this vector.
	 */
	public Vect2D normalise() {
		final double mag = mag();
		if (mag == 0){  // avoiding division by 0
			return this;
		}
		return new Vect2D(x/mag, y/mag);
	}

	/**
	 * magnitude
	 * @return magnitude of this vector
	 */
	public double mag() {
		return Math.hypot(x, y);
	}

	/**
	 * Rotates the current vector 90 degrees anticlockwise, doesn't modify this vector
	 * @return a copy of this vector, rotated 90 degrees anticlockwise
	 */
	public Vect2D rotate90degreesAnticlockwise() {
		// Note: this is meant to create a new vector and not modify the current vector
		//noinspection SuspiciousNameCombination
		return new Vect2D(-y, x);
	}

	/**
	 * rotate by angle given in radians
	 * (basically scalar rotation)
	 * @param angleRadians the angle to rotate this Vect2D by
	 * @return this Vect2D, rotated by angle radians.
	 */
	public Vect2D rotate(double angleRadians) {
		return new Vect2D(
				(x * Math.cos(angleRadians) - (y * Math.sin(angleRadians))),
				(x * Math.sin(angleRadians)) + (y * Math.cos(angleRadians))
		);
	}
}