package Engine;
import Utils.Color;
import Utils.Vector;

public class Raycast {
	Vector[] hitPositions;
	int hits;
	float objdistance;
	Color sumColor,	lightColor;
	
	public Raycast() {
		clear();
	}
	
	public void clear() {
		hitPositions = new Vector[RayTracer.maxRays];
		hits = 0;
		objdistance = 0;
		sumColor = Color.black.Clone();
		lightColor = Color.black.Clone();
	}
}