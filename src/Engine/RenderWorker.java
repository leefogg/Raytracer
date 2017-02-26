package Engine;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import Utils.Color;
import Utils.Position;

public class RenderWorker extends Thread {
	private static BufferedImage
	diffusemap,
	depthmap,
	lightmap;
	private static Scene workingscene;
	public static volatile int currentScanLine = 0;
	
	private static float distanceMultiplier = 1f / (100f / 255f);


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
		currentScanLine = 0;
	}
	
	public int getX() {
		return 0;
	}
	
	public int getY() {
		return currentScanLine;
	}

	@Override
	public void run() {
		int 
		width = diffusemap.getWidth(),
		height = diffusemap.getHeight();
		Raycast job = new Raycast();
		while (true) {
			++currentScanLine;
			int scanline = currentScanLine;
			if (scanline >= height)
				break;
			
			Color color;
			int depth;
			for (int x=0; x<width; x++) {
				job.clear();
				color = RayTracer.traceRay(job, workingscene.camera.getRay(x, scanline), 0);
				diffusemap.setRGB(x, scanline, color.getRGB());

				depth = (int)(job.objdistance*distanceMultiplier); // Depth is limited to 100 units
				if (depth > 255) depth = 255;
				depth = 255-depth;
				depthmap.setRGB(x, scanline, depth | (depth << 8) | (depth << 16));
				lightmap.setRGB(x, scanline, job.lightColor.getRGB());
				
				job.clear();
			}
		}
	}
}