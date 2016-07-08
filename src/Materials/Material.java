package Materials;
import Utils.Color;
import Utils.Vector;


public abstract class Material {
	public static final Material
	chrome = new ChromeMaterial(),
	checkerboard = new CheckerboardMaterial(),
	plastic = new PlasticMaterial(),
	error = new ErrorMaterial();
	
	public float roughness;
	
	public Material(float roughness) {
		this.roughness = roughness;
	}
	
	public abstract Color diffuseColor(Vector pos);
	public abstract Color ambientColor();
	public abstract Color specularColor(Vector pos);
	public abstract float reflectivity(Vector pos);
	// Glossy (lights showing using specular?)
}