package Objects;

import Utils.Color;
import Utils.Vector;
import Engine.Ray;

public final class AmbientLight extends Light {

	public AmbientLight(Vector pos, Color c) {
		super(pos, c);
	}

	@Override
	public float getBrightnessAtAngle(Ray ray) {
		return brightness;
	}
}