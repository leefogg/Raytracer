package Engine;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import Utils.Color;
import Utils.Position;

public class RenderWorker extends Thread {
	private static BufferedImage
	image,
	depthmap,
	lightmap;
	private static Scene scene;
	private static final ArrayList<Position> renderpositions = new ArrayList<Position>();
	public static volatile int renderPositionIndex = 0;
	
	public static final int 
	width = 25,
	height = 25;
	
	private static float distanceMultiplier = 1f / (100f / 255f);

	private int 
	startx = 0,
	starty = 0;


	public RenderWorker() {}
	
	public static void updateScene(Scene s) {
		scene = s;
	}
	
	public static void updateImages(BufferedImage img, BufferedImage dphmp, BufferedImage lgtmp) {
		image = img;
		depthmap = dphmp;
		lightmap = lgtmp;
	}
	
	public static void setDepthLimit(float maxdepth) {
		distanceMultiplier = 1f / (maxdepth / 255f);
	}
	
	public static void reset() {
		renderPositionIndex = 0;
	}
	
	public static void generateRenderPositions() {
		int x=0, y=0, index=0;
		while (true) {
			if (index < renderpositions.size()) {
				Position pos = renderpositions.get(index++);
				pos.x = x;
				pos.y = y;
			} else {
				renderpositions.add(new Position(x,y));
				index++;
			}
			x += RenderWorker.width;
			if (x >= image.getWidth()) {
				x = 0;
				y += RenderWorker.height;
			}
			if (y >= image.getHeight()) break;
		}
	}

	public int getX() {
		return startx;
	}

	public int getY() {
		return starty;
	}

	@Override
	public void run() {
		while (true) {
			if (renderpositions.size() <= renderPositionIndex) break;
			Position nextpos = renderpositions.get(renderPositionIndex++);
			startx = nextpos.x;
			starty = nextpos.y;

			int 
			endx = startx + width,
			endy = starty + height;
			if (endx > image.getWidth()) 	endx = image.getWidth();
			if (endy > image.getHeight())	endy = image.getHeight();
			
			Color color;
			Raycast job = new Raycast();
			int depth;
			for (int y=starty; y<endy; y++) {
				for (int x=startx; x<endx; x++) {
					color = RayTracer.traceRay(job, new Ray(scene.camera.position, scene.camera.getRayDirection(x, y)), 0);
					image.setRGB(x, y, color.getRGB());

					depth = (int)(job.objdistance*distanceMultiplier); // Depth is limited to 100 units
					if (depth > 255) depth = 255;
					depth = 255-depth;
					depthmap.setRGB(x, y, depth | (depth << 8) | (depth << 16));
					lightmap.setRGB(x, y, job.lightColor.getRGB());
					
					job.clear();
				}
			}

		}
	}
}