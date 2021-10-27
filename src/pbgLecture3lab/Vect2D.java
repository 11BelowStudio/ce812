package pbgLecture3lab;

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
	 * magnitude
	 * @return magnitude of this vector
	 */
	public double mag() {
		return Math.hypot(x, y);
	}

	/**
	 * The angle of this vector
	 * @return the angle of this vector
	 */
	public double angle() {
		return Math.atan2(y, x);
	}

	/**
	 * angle of difference vector between this vector and other vector
	 * @param other other vector
	 * @return angle between them
	 */
	public double angle(Vect2D other) {
		return Math.atan2(other.y - y, other.x - x);
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
	 * rotate by angle given in radians
	 * (basically scalar rotation)
	 * @param angle the angle to rotate this Vect2D by
	 * @return this Vect2D, rotated by angle radians.
	 */
	public Vect2D rotate(double angle) {
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		double nx = x * cos - y * sin;
		double ny = x * sin + y * cos;
		return new Vect2D(nx,ny);
	}

	/**
	 * Dot product of this vector and another one
	 * @param v other vector
	 * @return this dot v
	 */
	public double scalarProduct(Vect2D v) {
		return x * v.x + y * v.y;
	}

	/**
	 * Normalization
	 * @return normalized version of this vector. if this vector is 0,0, returns this vector.
	 */
	public Vect2D normalise() {
		double len = mag();
		return new Vect2D(x/len, y/len);
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
	 * Rotates the current vector 90 degrees anticlockwise, doesn't modify this vector
	 * @return a copy of this vector, rotated 90 degrees anticlockwise
	 */
	public Vect2D rotate90degreesAnticlockwise() {
		return new Vect2D(-y,x);
	}
}
