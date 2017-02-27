package Objects;
import Engine.Ray;
import Engine.RaycastReport;
import Materials.Material;
import Utils.Vector;


public final class Floor extends RenderObject {
	private static final Vector normal = Vector.up;
	
	public Floor(Material mat) {
		super(mat);
	}

	@Override
	public Vector getNormal(Vector pos) {
		return normal;
	}
	

	@Override
	public void intersect(RaycastReport report, Ray ray) {
		float denom = Vector.dot(normal, ray.direction);
		if (denom <= 0)
			report.check(this, ray, Vector.dot(normal, ray.origin) / -denom);
	}

}
