package Objects;

import Utils.Color;
import Utils.Vector;
import Engine.Ray;

public final class SpotLight extends Light {
	private Vector normal; // not created!
	private float angle;
	
	public SpotLight(Vector pos, Color c, Vector direction, float angle) {
		super(pos, c);
		direction.normalize();
		this.angle = (float)Math.cos(Math.toRadians(angle));
		System.out.println(angle);
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