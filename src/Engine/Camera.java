package Engine;
import Utils.Vector;

public final class Camera {
	public Vector 
	position,
	forward,
	right,
	up;
	
	private Ray[][] rays;
	
	public Camera(Vector pos, Vector lookat) {
		setPosition(pos);
		lookAt(lookat);
	}
	
	public void setPosition(Vector newpos) {
		this.position = newpos;
	}
	
	public void lookAt(Vector lookatpos) {
		this.forward = Vector.normal(Vector.subtract(lookatpos, position));
		this.right = Vector.scale(1.5f, Vector.normal(Vector.cross(forward, Vector.down)));
		this.up = Vector.scale(1.5f, Vector.normal(Vector.cross(forward, right)));
	}
	
	public void cacheRays(int width, int height) {
		rays = new Ray[height][width];
		for (int y=0; y<height; y++)
			for (int x=0; x<width; x++)
				rays[y][x] = new Ray(position, screenToRayDirection(x, y, width, height));
	}
	
	public Ray getRay(int x, int y) {
		return rays[y][x];
	}
	
	private Vector screenToRayDirection(int x, int y, int width, int height) {
		float x2 = (x - (width / 2f)) / 2f / width;
		float y2 = -(y - (height / 2f)) / 2f / height;
		return new Vector(
						forward.x + (up.x * y2) + (right.x * x2), 
						forward.y + (up.y * y2) + (right.y * x2), 
						forward.z + (up.z * y2) + (right.z * x2)
				).normalize();
	}
}