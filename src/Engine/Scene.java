package Engine;

import java.util.ArrayList;

import Objects.Light;
import Objects.RenderObject;
import Utils.Subdivmap;
import Utils.Vector;

public final class Scene {
	public RenderObject[] objects;
	public Light[] lights;
	public Camera camera;
	
	private Subdivmap map;
	
	public Scene() {
		this(new RenderObject[]{}, new Light[]{});
	}
	public Scene(RenderObject[] objs) {
		this(objs, new Light[]{});
	}
	public Scene(RenderObject[] objs, Light[] lts) {
		objects = objs;
		lights = lts;
	}
	
	public void add(RenderObject o) {
		RenderObject[] newobjects = new RenderObject[objects.length+1];
		for (int i=0; i<objects.length; i++)
			newobjects[i] = objects[i];
		newobjects[objects.length] = o;
		objects = newobjects;
	}
	
	public void add(Light o) {
		Light[] newlights = new Light[lights.length+1];
		for (int i=0; i<lights.length; i++)
			newlights[i] = lights[i];
		newlights[lights.length] = o;
		lights = newlights;
	}
	
	public void buildMap() {
//		map = new Subdivmap(this);
//		System.out.println(map);
//		Ray r = new Ray(new Vector(0, 7, -10), new Vector(0,0,1));
//		for (RenderObject obj : findObjects(r)) 
//			System.out.println(obj.ID);
	}
	
	ArrayList<RenderObject> findObjects(Ray ray) {
		ArrayList<RenderObject> objs = new ArrayList<RenderObject>();
		boolean[] found = new boolean[objects.length];
		Vector testposition = ray.origin.Clone();
		while(map.isInBounds(testposition.x, testposition.y, testposition.z)) {
			System.out.println(testposition);
			testposition.add(ray.direction);
			for (RenderObject obj : map.getObjects(testposition.x, testposition.y, testposition.z))
				if (!found[obj.ID]) {
					found[obj.ID] = true;
					objs.add(obj);
				}
		}
		return objs;
	}
}