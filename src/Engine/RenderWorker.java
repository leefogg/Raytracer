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
	private static final ArrayList<Position> renderpositions = new ArrayList<Position>();
	public static volatile int renderPositionIndex = 0;
	
	public static int 
	sectorWidth,
	sectorHeight;
	
	private static float distanceMultiplier = 1f / (100f / 255f);

	// TODO: Change to one scanline per worker
	private int 
	startx = 0,
	starty = 0;


	public RenderWorker() {}
	
	public static void updateScene(Scene scene) {
		workingscene = scene;
	}
	
	public static void updateImages(BufferedImage diffuse, BufferedImage depth, BufferedImage light) {
		diffusemap = diffuse;
		depthmap = depth;
		lightmap = light;
		
		sectorWidth = diffuse.getWidth() >> 4;
		sectorHeight = diffuse.getHeight() >> 4;
	}
	
	public static void setDepthLimit(float maxdepth) {
		distanceMultiplier = 1f / (maxdepth / 255f);
	}
	
	public static void reset() {
		renderPositionIndex = 0;
	}
	
	public static void generateRenderPositions() {
		int 
		x=0, 
		y=0, 
		index=0,
		width = diffusemap.getWidth(),
		height = diffusemap.getHeight();
		while (true) {
			if (index < renderpositions.size()) { // If this position already exists, reuse it
				Position pos = renderpositions.get(index);
				pos.x = x;
				pos.y = y;
			} else { // Otherwise create a new one
				renderpositions.add(new Position(x,y));
			}
			index++;
			
			x += RenderWorker.sectorWidth;
			if (x >= width) {
				x = 0;
				y += RenderWorker.sectorHeight;
			}
			if (y >= height) break;
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
		int 
		width = diffusemap.getWidth(),
		height = diffusemap.getHeight();
		while (true) {
			if (renderpositions.size() <= renderPositionIndex)
				break;
			Position nextpos = renderpositions.get(renderPositionIndex++);
			startx = nextpos.x;
			starty = nextpos.y;

			int 
			endx = startx + sectorWidth,
			endy = starty + sectorHeight;
			if (endx > width) 	endx = width;
			if (endy > height)	endy = height;
			
			Color color;
			Raycast job = new Raycast();
			int depth;
			for (int y=starty; y<endy; y++) {
				for (int x=startx; x<endx; x++) {
					color = RayTracer.traceRay(job, workingscene.camera.getRay(x, y), 0);
					diffusemap.setRGB(x, y, color.getRGB());

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