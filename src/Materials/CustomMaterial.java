package Materials;

import Utils.Color;
import Utils.Vector;

public final class CustomMaterial extends Material {
	private Color diffuse, specular, ambient;
	private float reflectivity;
	
	public CustomMaterial(Color diffuse, Color specular, Color ambient, float reflectivity, float roughness) {
		super(roughness);
		this.diffuse = diffuse;
		this.ambient = ambient;
		this.specular = specular;
		this.reflectivity = reflectivity;
	}

	@Override
	public Color diffuseColor(Vector pos) {
		return diffuse;
	}
	
	@Override
	public Color ambientColor() {
		return ambient;
	}

	@Override
	public Color specularColor(Vector pos) {
		return specular;
	}

	@Override
	public float reflectivity(Vector pos) {
		return reflectivity;
	}
}