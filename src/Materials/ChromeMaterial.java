package Materials;
import Utils.Color;
import Utils.Vector;



public final class ChromeMaterial extends Material {
	public ChromeMaterial() {
		super(2100f);
	}
	
	@Override
	public Color diffuseColor(Vector pos) {
		return Color.black;
	}
	
	@Override
	public Color ambientColor() {
		return Color.black;
	}

	@Override
	public Color specularColor(Vector pos) {
		return Color.white;
	}

	@Override
	public float reflectivity(Vector pos) {
		return 0.7f;
	}
}
