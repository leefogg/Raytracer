package Engine;
import Objects.RenderObject;

public final class RaycastReport {
	public RenderObject object;
	public Ray ray;
	public float distance;
	
	public RaycastReport(RenderObject object, Ray ray, float distance) {
		this.object = object;
		this.ray = ray;
		this.distance = distance;
	}
}
