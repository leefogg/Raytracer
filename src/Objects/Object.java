package Objects;
import Engine.Ray;
import Engine.RaycastReport;
import Materials.Material;
import Utils.Vector;

public abstract class Object {
	public Material material;
	public Object(Material mat) {
		material = mat;
	}
	public abstract Vector getNormal(Vector pos);
	public abstract RaycastReport intersect(Ray ray);
}