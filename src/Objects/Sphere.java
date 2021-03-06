package Objects;
import Engine.Ray;
import Engine.RaycastReport;
import Materials.Material;
import Utils.Vector;


public final class Sphere extends RenderObject {
	private float radiussquared;
	
	public Sphere(Vector pos, float radius, Material mat) {
		super(mat);
		position = pos;
		this.radiussquared = radius * radius;
		this.halfsize = new Vector(radius, radius, radius);
	}

	@Override
	public Vector getNormal(Vector pos) {
		return Vector.subtract(pos, position).normalize();
	}

	@Override
	public void intersect(RaycastReport report, Ray ray) {
		Vector eo = Vector.subtract(position, ray.origin);
		float v = Vector.dot(eo, ray.direction);
		float dist = 0;
		if (v >= 0) {
			float disc = radiussquared - (Vector.dot(eo, eo) - v * v);
			if (disc >= 0)
				dist = v - disc;
		}
		
		if (dist > 0) 
			report.check(this, ray, dist);
	}

}