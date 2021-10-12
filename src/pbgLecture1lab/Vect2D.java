package pbgLecture1lab;


/**
 * A 2D Vector
 *
 * Based on the CE218 Vector2D class, but modified for PBG course to be IMMUTABLE
 */
public final class Vect2D {

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
		// TODO insert code here to return a new vector that is the sum of this vector plus vector v
		throw new RuntimeException("Not finished");
	}


	/**
	 * Scaled addition of two vectors.
	 * note: vector subtraction can be expressed as scaled addition with factor (-1)
	 * @param v the other vector
	 * @param fac how much that other vector will be scaled by
	 * @return a vector equal to this + (v * fac)
	 */
	public Vect2D addScaled(Vect2D v, double fac) {
		// TODO insert code here to return a new vector equal to "this + v * fac"
		throw new RuntimeException("Not finished");
	}

	/**
	 * Returns a new vector equal to this*fac
	 * @param fac how much to multiply this vector by
	 * @return result of this * fac
	 */
	public Vect2D mult(double fac) {
		// TODO insert code here to return a new vector equal to "this * fac"
		throw new RuntimeException("Not finished");
	}

}
