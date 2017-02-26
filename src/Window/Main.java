package Window;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JPanel;

import Engine.Camera;
import Engine.RayTracer;
import Engine.Scene;
import Materials.Material;
import Objects.AmbientLight;
import Objects.Floor;
import Objects.Light;
import Objects.Mesh;
import Objects.RenderObject;
import Objects.Sphere;
import Utils.Color;
import Utils.Framecounter;
import Utils.Vector;

public class Main extends JPanel {
	private static final long serialVersionUID = 1435209921473617233L;
	
	private Scene scene;
	private BufferedImage image = new BufferedImage(512, 512, BufferedImage.TYPE_INT_RGB);
	private float sincos = 0;
	private Sphere[] spheres = new Sphere[11];
	private Mesh mesh;
	private Framecounter  fc = new Framecounter();
	private int frame = 0;
	
	public Main() {
		spheres[0] = new Sphere(new Vector(0,2,0), 1, Material.chrome);
		for (int i=1; i<spheres.length; i++)
			spheres[i] = new Sphere(new Vector(0,0,0), 0.5f, Material.plastic);
		
		RenderObject[] objects = new RenderObject[spheres.length+1];
		objects[0] = new Floor(Material.checkerboard);
		for (int i=1; i<objects.length; i++)
			objects[i] = spheres[i-1];
//		
//		try {
//			mesh = new Mesh("res/box.obj", Material.error);
//			//mesh.scale(3,3,3);
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
		
		try {
			scene = new Scene(
						objects,
						new Light[] {
								new AmbientLight(new Vector(-2f, 2.5f, 0f), 	 new Color(0.25f, 0.05f, 0.05f)),
								new AmbientLight(new Vector(1.5f, 2.5f, 1.5f),	 new Color(0.05f, 0.05f, 0.25f)),
								new AmbientLight(new Vector(1.5f, 2.5f, -1.5f),  new Color(0.05f, 0.25f, 0.05f)),
								new AmbientLight(new Vector(0f, 3.5f, 0f), 	 	 new Color(0.20f, 0.20f, 0.20f)),
								//new SpotLight(new Vector(0,10,0), Color.white, new Vector(0,-1,0), 70)
						}
					);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		RayTracer.init(scene, image);
		scene.camera = new Camera(new Vector(0, 7, -10), new Vector(0,2,0));
		scene.camera.cacheRays(image.getWidth(), image.getHeight());
		//scene.buildMap();
	}

	public void paintComponent(Graphics canvas) {
//		if (renderer.isRendering()) return;
		
		update();
		canvas.drawImage(RayTracer.diffusemap, 0, 0, this);
		//renderer.drawRenderingThreads(canvas);
		canvas.setColor(java.awt.Color.white);
		canvas.drawString(String.valueOf(fc.FPS)  +" FPS", 5, 15);
		canvas.drawString("Frame: " + String.valueOf(frame++), 5, 25);
		canvas.drawString("Iterations: " + RayTracer.iterations, 5, 45);
		canvas.drawString("Rays: " + RayTracer.rays + " (" + String.valueOf((float)RayTracer.rays/(image.getWidth()*image.getHeight())) +  " per pixel)", 5, 65);
		
		fc.newframe();
		repaint();
	}

	private void update() {
		if (RayTracer.isRendering()) return;
		
		sincos += (float)(Math.PI*2/720);
		
//		scene.camera.setPosition(new Vector((float)-Math.cos(sincos)*10, 7, (float)Math.sin(sincos)*10));
//		scene.camera.lookAt(new Vector(0f, 2.0f, 0.0f));
		
		float sincosbackup = sincos;
		//mesh.rotate(1f,1f,1f);
		float height = sincos;
		for (int i=1; i<spheres.length; i++) {
			sincos += (float)(Math.PI*2/(spheres.length-1));
			height += (float)(Math.PI*8/(spheres.length-1));
			spheres[i].position = new Vector((float)Math.cos(sincos)*3, 2.5f+(float)Math.cos(height)*1.5f, (float)Math.sin(sincos)*3);
		}
		sincos = sincosbackup;
			
		try {
			RayTracer.startRendering().join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}