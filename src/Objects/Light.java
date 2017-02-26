package Objects;

import Engine.Ray;
import Utils.Color;
import Utils.Vector;

public abstract class Light {
	public Vector position;
	public Color color;
	public float brightness = 1;
	
	public Light(Vector pos, Color color) {
		this(pos, color, 1);
	}
	public Light(Vector pos, Color c, float brightness) {
		position = pos;
		color = c;
		this.brightness = brightness;
	}
	
	public abstract float getBrightnessAtAngle(Ray ray);
}