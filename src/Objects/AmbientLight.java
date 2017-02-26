package Objects;

import Utils.Color;
import Utils.Vector;
import Engine.Ray;

public final class AmbientLight extends Light {

	public AmbientLight(Vector pos, Color color) {
		super(pos, color);
	}

	@Override
	public float getBrightnessAtAngle(Ray ray) {
		return brightness;
	}
}