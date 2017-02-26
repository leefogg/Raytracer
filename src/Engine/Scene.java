package Engine;

import java.util.ArrayList;

import Objects.Light;
import Objects.RenderObject;
import Utils.Vector;

public final class Scene {
	public RenderObject[] objects;
	public Light[] lights;
	public Camera camera;
	
	public Scene() {
		this(new RenderObject[]{}, new Light[]{});
	}
	public Scene(RenderObject[] objs) {
		this(objs, new Light[]{});
	}
	public Scene(RenderObject[] objects, Light[] lights) {
		this.objects = objects;
		this.lights = lights;
	}
	
	public void add(RenderObject object) {
		RenderObject[] newobjects = new RenderObject[objects.length+1];
		for (int i=0; i<objects.length; i++)
			newobjects[i] = objects[i];
		newobjects[objects.length] = object;
		objects = newobjects;
	}
	
	public void add(Light light) {
		Light[] newlights = new Light[lights.length+1];
		for (int i=0; i<lights.length; i++)
			newlights[i] = lights[i];
		newlights[lights.length] = light;
		lights = newlights;
	}
	
	public void buildMap() {
//		map = new Subdivmap(this);
//		System.out.println(map);
//		Ray r = new Ray(new Vector(0, 7, -10), new Vector(0,0,1));
//		for (RenderObject obj : findObjects(r)) 
//			System.out.println(obj.ID);
	}
	
}