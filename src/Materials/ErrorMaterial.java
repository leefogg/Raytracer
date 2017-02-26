package Materials;

import Utils.Color;
import Utils.Vector;

public final class ErrorMaterial extends Material {

	public ErrorMaterial() {
		super(1000);
	}

	@Override
	public Color diffuseColor(Vector pos) {
		return Color.black.Clone();
	}
	
	@Override
	public Color ambientColor() {
		return new Color(1,0,1);
	}
	
	@Override
	public Color specularColor(Vector pos) {
		return Color.white.Clone();
	}

	@Override
	public float reflectivity(Vector pos) {
		return 0;
	}
}
