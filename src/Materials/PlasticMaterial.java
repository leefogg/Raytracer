package Materials;

import Utils.Color;
import Utils.Vector;

public final class PlasticMaterial extends Material {

	public PlasticMaterial() {
		super(500);
	}

	@Override
	public Color diffuseColor(Vector pos) {
		return Color.white;
	}

	@Override
	public Color ambientColor() {
		return Color.grey;
	}
	
	@Override
	public Color specularColor(Vector pos) {
		return Color.black;
	}

	@Override
	public float reflectivity(Vector pos) {
		return 0;
	}

}
