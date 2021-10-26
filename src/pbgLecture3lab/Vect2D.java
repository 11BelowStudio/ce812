package pbgLecture3lab;

import java.io.Serializable;

public final class Vect2D implements Serializable {
	// Based on the Vector2D class, but modified for PBG course to be IMMUTABLE

	public final double x, y;

	// create a null vector
	public Vect2D() {
		this(0, 0);
	}

	// create vector with given components
	public Vect2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	// create new vector that is a copy of the argument
	public Vect2D(Vect2D v) {
		this(v.x, v.y);
	}

	public boolean equals(Object o) {
		if (o instanceof Vect2D) {
			Vect2D v = (Vect2D) o;
			return x == v.x && y == v.y;
		} else
			return false;
	}

	public double mag() {
		return Math.hypot(x, y);
	}

	public double angle() {
		return Math.atan2(y, x);
	}

	// angle of difference vector between this vector and other vector
	public double angle(Vect2D other) {
		return Math.atan2(other.y - y, other.x - x);
	}

	public String toString() {
		return "(" + String.format("%.01f", x) + "," + String.format("%.01f", y)
				+ ")";
	}

	public Vect2D add(Vect2D v) {
		return new Vect2D(this.x+v.x, this.y+v.y);
	}

	// scaled addition - surprisingly useful
	// note: vector subtraction can be expressed as scaled addition with factor
	// (-1)
	public Vect2D addScaled(Vect2D v, double fac) {
		return new Vect2D(this.x + v.x * fac, this.y + v.y * fac);
	}

	public Vect2D mult(double fac) {
		return new Vect2D(this.x * fac,this.y * fac);
	}

	public Vect2D rotate(double angle) {
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		double nx = x * cos - y * sin;
		double ny = x * sin + y * cos;
		return new Vect2D(nx,ny);
	}

	public double scalarProduct(Vect2D v) {
		return x * v.x + y * v.y;
	}

	public Vect2D normalise() {
		double len = mag();
		return new Vect2D(x/len, y/len);
	}

	public static Vect2D minus(Vect2D v1, Vect2D v2) {
		// returns v1-v2
		return v1.addScaled(v2, -1);
	}
	
	public Vect2D rotate90degreesAnticlockwise() {
		return new Vect2D(-y,x);
	}
}
