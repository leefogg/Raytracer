package Materials;
import Utils.Color;
import Utils.Vector;



public final class CheckerboardMaterial extends Material {

	public CheckerboardMaterial() {
		super(1100f);
	}
	
	@Override
	public Color diffuseColor(Vector pos) {
		if ((Math.floor(pos.z) + Math.floor(pos.x)) % 2 != 0)
			return Color.white.Clone();
		return Color.black.Clone();
	}
	
	@Override
	public Color ambientColor() {
		return Color.black.Clone();
	}

	@Override
	public Color specularColor(Vector pos) {
		return Color.white.Clone();
	}

	@Override
	public float reflectivity(Vector pos) {
		if ((Math.floor(pos.z) + Math.floor(pos.x)) % 2 != 0)
			return 0.2f;
		return 0.025f;
	}
}
