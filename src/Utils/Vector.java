package Utils;


public final class Vector {
	public static final Vector
	forward = new Vector(0,0,1),
	back 	= new Vector(0,-1,0),
	left 	= new Vector(-1,0,0),
	right	= new Vector(1,0,0),
	up 		= new Vector(0,1,0),
	down 	= new Vector(0,-1,0);
	
	public float x, y, z;
	
	public Vector() {
		
	}

	public Vector(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector multiply(float scale) {
		x *= scale;
		y *= scale;
		z *= scale;
		return this;
	}
	
	public Vector multiply(Vector other) {
		x *= other.x;
		y *= other.y;
		z *= other.z;
		return this;
	}
	
	public static Vector scale(float scale, Vector v) {
		return new Vector(v.x * scale, v.y * scale, v.z * scale);
	}
	
	public void subtract(Vector other) {
		x -= other.x;
		y -= other.y;
		z -= other.z;
	}
	
	public static Vector subtract(Vector v1, Vector v2) {
		return new Vector(
				v1.x - v2.x, 
				v1.y - v2.y, 
				v1.z - v2.z);
	}
	
	public Vector add(Vector other) {
		x += other.x;
		y += other.y;
		z += other.z;
		return this;
	}
	
	public Vector negate() {
		x = -x;
		x = -y;
		z = -z;
		return this;
	}
	
	public Vector normalize() {
		float dist = realDistance();
		float div = (dist == 0) ? Float.POSITIVE_INFINITY : 1f / dist;
		multiply(div);
		return this;
	}
	
	public static Vector add(Vector v1, Vector v2) {
		return new Vector(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
	}
	
	public float dot(Vector other) {
		return x * other.x + y * other.y + z * other.z;
	}
	
	public static float dot(Vector v1, Vector v2) {
		return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
	}
	
	public static float realDistance(Vector v) {
		return (float)Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
	}
	
	public float realDistance() {
		return (float)Math.sqrt(x*x + y*y + z*z);
	}
	
	public static float distance(Vector v) {
		return (float)(v.x * v.x + v.y * v.y + v.z * v.z);
	}
	
	public static Vector normal(Vector other) {
		float dist = realDistance(other);
		float div = (dist == 0) ? Float.POSITIVE_INFINITY : 1f / dist;
		return Vector.scale(div, other);
	}
	
	public Vector rotate(float x, float y, float z) {
		Vector vert = this.rotateX(x);
		vert = vert.rotateY(y);
		return vert.rotateZ(z);
	}
	public Vector rotateX(float angle) {
		float
		rad = (float)Math.toRadians(angle),
		cosa = (float)Math.cos(rad),
		sina = (float)Math.sin(rad);
		return new Vector(z * sina + x * cosa, y, z * cosa - x * sina);
	}
	public Vector rotateY(float angle) {
		float
		rad = (float)Math.toRadians(angle),
		cosa = (float)Math.cos(rad),
		sina = (float)Math.sin(rad);
		return new Vector(x, y * cosa - z * sina, y * sina + z * cosa);
	}
	public Vector rotateZ(float angle) {
		float
		rad = (float)Math.toRadians(angle),
		cosa = (float)Math.cos(rad),
		sina = (float)Math.sin(rad);
		return new Vector(x * cosa - y * sina, x * sina + y * cosa, z);
	}
	
	public Vector cross(Vector other) {
		float 
		X = y * other.z - other.y * z,
		Y = z * other.x - other.z * x,
		Z = x * other.y - other.x * y;
		x = X;
		y = Y;
		z = Z;
		return this;
	}
	public static Vector cross(Vector v1, Vector v2) {
		return new Vector(
					v1.y * v2.z - v2.y * v1.z,
					v1.z * v2.x - v2.z * v1.y,
					v1.x * v2.y - v2.x * v1.y
				);
	}
	
	public Vector Clone() {
		return new Vector(x,y,z); 
	}
	@Override
	public String toString() {
		return "X: " + x + "\tY: " + y + "\tZ: " + z;
	}
}