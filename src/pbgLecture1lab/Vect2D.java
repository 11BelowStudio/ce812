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
		return new Vect2D(x * fac, y * fac);
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

	/**
	 * Returns a polar vector with given angle (in radians) and magnitude
	 * @param angleRadians the angle for the vector
	 * @param mag the length of the vector
	 * @return that polar vector.
	 */
	public static Vect2D POLAR_VECT(double angleRadians, double mag){
		return new Vect2D(mag*Math.cos(angleRadians),mag*Math.sin(angleRadians));
	}

	/**
	 * Compares this to another object to see if it's equal
	 * @param o the other object
	 * @return true if the other object is also a Vect2D and has equal x and y
	 */
	@Override
	public boolean equals(Object o){
		if (o instanceof Vect2D){
			final Vect2D v = (Vect2D) o;
			return ((x == v.x) && (y == v.y));
		}
		return false;
	}

}
