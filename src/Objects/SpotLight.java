package Objects;

import Utils.Color;
import Utils.Vector;
import Engine.Ray;

public final class SpotLight extends Light {
	private Vector normal; // not created!
	private float angle;
	
	public SpotLight(Vector pos, Color color, Vector direction, float angle) {
		super(pos, color);
		direction.normalize();
		this.angle = (float)Math.cos(Math.toRadians(angle));
	}

	@Override
	public float getBrightnessAtAngle(Ray ray) {
		float anglediff = ray.direction.normalize().dot(normal);
		if (anglediff < -angle)
			anglediff = -angle-anglediff;
		else
			anglediff = 0;
		return anglediff;
	}
}