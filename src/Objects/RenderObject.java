package Objects;
import Engine.Ray;
import Engine.RaycastReport;
import Materials.Material;
import Utils.Vector;

public abstract class RenderObject {
	public Vector 
	position = new Vector(0,0,0),
	halfsize = new Vector(1,1,1);
	
	public Material material = Material.error;
	
	private static int currentID = 0;
	public int ID = currentID++;
	
	public RenderObject() {
		material = Material.error;
	}
	
	public RenderObject(Material mat) {
		material = mat;
	}
	
	public abstract Vector getNormal(Vector pos);
	public abstract void intersect(RaycastReport report, Ray ray);
}