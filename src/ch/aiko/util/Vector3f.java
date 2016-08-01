package ch.aiko.util;

public class Vector3f {

	public float x;
	public float y;
	public float z;

	public Vector3f() {
		x = y = z = 0;
	}

	public Vector3f(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void normalize() {
		double p = Math.pow(x * x + y * y + z * z, 0.5F);
		x /= p;
		y /= p;
		z /= p;
	}

}
