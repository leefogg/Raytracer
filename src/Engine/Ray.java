package Engine;
import Utils.Vector;


public final class Ray {
	public Vector origin, direction;

	public Ray(Vector position, Vector direction) {
		this.origin = position;
		this.direction = direction;
	}
}
