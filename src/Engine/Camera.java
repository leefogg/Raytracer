package Engine;
import Utils.Vector;

public final class Camera {
	public Vector 
	position,
	forward,
	right,
	up;
	
	private Vector[][] raydirections;
	
	public Camera(Vector pos, Vector lookat) {
		setPosition(pos);
		lookAt(lookat);
	}
	
	public void setPosition(Vector newpos) {
		this.position = newpos;
	}
	
	public void lookAt(Vector lookatpos) {
		Vector down = new Vector(0,-1,0);
		this.forward = Vector.normal(Vector.subtract(lookatpos, position));
		this.right = Vector.scale(1.5f, Vector.normal(Vector.cross(forward, down)));
		this.up = Vector.scale(1.5f, Vector.normal(Vector.cross(forward, right)));
	}
	
	public void cacheRays(int width, int height) {
		raydirections = new Vector[height][width];
		for (int y=0; y<height; y++)
			for (int x=0; x<width; x++)
				raydirections[y][x] = screen2Ray(x, y, width, height);
	}
	
	public Vector getRayDirection(int x, int y) {
		return raydirections[y][x];
	}
	
	private Vector screen2Ray(int x, int y, int width, int height) {
		float x2 = (x - (width / 2f)) / 2f / width;
		float y2 = -(y - (height / 2f)) / 2f / height;
		//return Vector.normal(Vector.add(forward, Vector.add(Vector.multiply(x2, right), Vector.multiply(y2, up))));
		return Vector.normal(
				new Vector(
						forward.x + (up.x * y2) + (right.x * x2), 
						forward.y + (up.y * y2) + (right.y * x2), 
						forward.z + (up.z * y2) + (right.z * x2)
				)
			);
	}
}