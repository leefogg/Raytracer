package Engine;
import Utils.Color;
import Utils.Vector;

public class Raycast {
	Vector[] hitPositions = new Vector[RayTracer.maxRays];
	int hits;
	float objdistance;
	Color 
	sumColor = Color.black.Clone(),
	lightColor = Color.black.Clone();
	
	public Raycast() {
		clear();
	}
	
	public void clear() {
		hits = 0;
		objdistance = 0;
		sumColor.set(0, 0, 0);
		lightColor.set(0, 0, 0);
	}
}