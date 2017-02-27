package Engine;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import Utils.Color;
import Utils.Position;

public class RenderWorker extends Thread {
	private static BufferedImage
	diffusemap,
	depthmap,
	lightmap;
	private static Scene workingscene;
	private static float distanceMultiplier = 1f / (100f / 255f);
	public static AtomicInteger currentScanLine = new AtomicInteger(0);
	public volatile boolean done, running;
	
	int scanline;

	public RenderWorker() {}
	
	public static void updateScene(Scene scene) {
		workingscene = scene;
	}
	
	public static void updateImages(BufferedImage diffuse, BufferedImage depth, BufferedImage light) {
		diffusemap = diffuse;
		depthmap = depth;
		lightmap = light;
	}
	
	public static void setDepthLimit(float maxdepth) {
		distanceMultiplier = 1f / (maxdepth / 255f);
	}
	
	public static void reset() {
		currentScanLine.set(0);
	}
	
	public int getX() {
		return 0;
	}
	
	public int getY() {
		return scanline;
	}

	@Override
	public void run() {
		int 
		width = diffusemap.getWidth(),
		height = diffusemap.getHeight();
		Raycast job = new Raycast();
		
		running = true;
		while (running) {
			currentScanLine.incrementAndGet();
			scanline = currentScanLine.get();
			if (scanline >= height) {
				done = true;
				while(done){
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// Interruption expected, do nothing
					}
					done = false;
				}
				continue;
			}
			
			Color color;
			int depth;
			for (int x=0; x<width; ++x) {
				job.clear();
				color = RayTracer.traceRay(job, workingscene.camera.getRay(x, scanline), 0);
				diffusemap.setRGB(x, scanline, color.getRGB());

				depth = (int)(job.objdistance*distanceMultiplier); // Depth is limited to 100 units
				if (depth > 255) 
					depth = 255;
				depth = 255-depth;
				depthmap.setRGB(x, scanline, depth | (depth << 8) | (depth << 16));
				lightmap.setRGB(x, scanline, job.lightColor.getRGB());
			}
		}
		
		running = false;
	}
}