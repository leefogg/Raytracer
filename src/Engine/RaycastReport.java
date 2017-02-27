package Engine;
import Objects.RenderObject;

public final class RaycastReport {
	RenderObject closestObject;
	Ray ray;
	float distance = Float.MAX_VALUE;
	boolean hitAny;
	
	public void set(RenderObject object, Ray ray, float distance) {
		this.closestObject = object;
		this.ray = ray;
		this.distance = distance;
	}
	
	public void check(RenderObject object, Ray ray, float distance) {
		if (distance < this.distance) {
			set(object, ray, distance);
			hitAny = true;
		}
	}
	
	public void clear() {
		closestObject = null;
		ray = null;
		distance = Float.MAX_VALUE;
		hitAny = false;
	}
	
	public RaycastReport Clone() {
		RaycastReport report = new RaycastReport();
		report.closestObject = this.closestObject;
		report.ray = this.ray;
		report.distance = this.distance;
		report.hitAny = this.hitAny;
		
		return report;
	}
}
