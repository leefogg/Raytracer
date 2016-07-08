package Engine;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import Objects.Light;
import Objects.RenderObject;
import Utils.Color;
import Utils.Position;
import Utils.Vector;

public final class RayTracer {
	private static Scene scene;
	public static BufferedImage
	image,
	depthmap,
	lightmap;
	
	private static RenderWorker[] workers = new RenderWorker[Math.max(1, Runtime.getRuntime().availableProcessors()-1)];
	private static boolean rendering = false;
	
	public static boolean shadows = true;
	public static final int maxRays = 2;
	public static int iterations, rays;
	
	public static void init(Scene sn, BufferedImage img) {
		scene = sn;
		image = img;
		depthmap = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		lightmap = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		
		RenderWorker.updateScene(scene);
		RenderWorker.updateImages(image, depthmap, lightmap);
		RenderWorker.generateRenderPositions();
	}
	
	public void setImageSize(int width, int height) {
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		depthmap = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		lightmap = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		RenderWorker.updateImages(image, depthmap, lightmap);
		RenderWorker.generateRenderPositions();
	}
	
	public static void setScene(Scene s) {
		scene = s;
		
		RenderWorker.updateScene(scene);
	}

	private static RaycastReport intersectScene(Ray ray) {
		float closest = Float.MAX_VALUE;
		RaycastReport closestObject = null;
		for (RenderObject object : scene.objects) {
			RaycastReport intersection = object.intersect(ray);
			if (intersection == null) 
				continue;
			
			if (intersection.distance < closest) {
				closestObject = intersection;
				closest = intersection.distance;
			}
		}
		
		return closestObject;
	}
	
	public static Color traceRay(Raycast job, Ray ray, int depth) {  //TODO: Copy traceRay method for use in viewport that doesn't reccur or shade
		iterations++;
		if (depth >= maxRays) return job.sumColor;
		
		RaycastReport intersections = intersectScene(ray);
		if (intersections == null) // Stop tracing if no hit
			return Color.voidColor;
		
		if (depth == 0) job.objdistance = intersections.distance;
		job.hits = depth;
		Color hitcolor = shade(job, intersections, depth);
		job.sumColor.add(hitcolor);
		
		return hitcolor;
	}

	private static Color shade(Raycast job, RaycastReport report, int depth) {
		Vector direction = report.ray.direction.Clone();
		Vector hitposition = Vector.scale(report.distance, direction).add(report.ray.origin);
		job.hitPositions[depth] = hitposition;
		Vector normal = report.object.getNormal(hitposition);
		direction.subtract(Vector.scale(Vector.dot(normal, direction) * 2, normal));
		Color reflectedcolor = getReflectionColor(job, report.object, hitposition, direction, depth);
		Color naturalcolor = light(job, report, hitposition, normal, direction, depth);
		return Color.add(naturalcolor, reflectedcolor);
	}

	private static Color getReflectionColor(Raycast job, RenderObject object, Vector pos, Vector reflectdirection, int depth) {
		float reflectivity = object.material.reflectivity(pos);
		if (reflectivity == 0) return Color.black;
		rays++;
		
		return Color.scale(reflectivity, traceRay(job, new Ray(pos, reflectdirection), ++depth));
	}

	private static Color light(Raycast job, RaycastReport hit, Vector hitpos, Vector normal, Vector reflectdirection, int depth) {
		Color outputColor = hit.object.material.ambientColor();
		for (Light light : scene.lights) {
			Vector
			lightDirection = Vector.subtract(light.position, hitpos),
			lightDirectionNorm = Vector.normal(lightDirection);
			
			if (shadows) {
				RaycastReport toLight = intersectScene(new Ray(hitpos, lightDirectionNorm));
				rays++;
				boolean isinshadow = (toLight == null) ? false : (toLight.distance <= Vector.distance(lightDirection));
				if (isinshadow) 
					continue;
			}
			
			float phumbrella = light.getBrightnessAtAngle(hit.ray);
			if (phumbrella == 0) 
				continue;
			
			float illumination = Vector.dot(lightDirectionNorm, normal);
			illumination *= phumbrella;
			float specular = Vector.dot(lightDirectionNorm, reflectdirection.normalize());
			Color 
			lightColor = (illumination > 0) ? Color.scale(illumination, light.color) : Color.defaultcolor,
			specularColor = (specular > 0) ? Color.scale((float)Math.pow(specular, hit.object.material.roughness), light.color) : Color.defaultcolor; // Pow? Not scale?
			if (depth == 0) job.lightColor.add(lightColor);
			lightColor.multiply(hit.object.material.diffuseColor(hitpos));
			specularColor.multiply(hit.object.material.specularColor(hitpos));
			lightColor.add(specularColor);
			lightColor.scale(light.brightness);
			outputColor = Color.add(outputColor, lightColor);
		}
		
		iterations += scene.lights.length;
		
		return outputColor;
	}

	
	public static Thread startRendering() {
		iterations = 0;
		rays = 0;
		
		RenderWorker.reset();
		
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				rendering = true;
				
				for (int i=0; i<workers.length; i++)
					workers[i] = new RenderWorker();
				
				for (RenderWorker w : workers)
					w.start();
				for (RenderWorker w : workers)
					while(w.isAlive()){}
				
				rendering = false;
			}
		});
		
		th.start();
		
		return th;
	}

	// Completely impractical to render with a single thread. Only used for debugging.
	public static void render(int startx, int starty, int width, int height) {
		iterations = 0;
		rays = 0;
		
		int 
		endx = startx + width,
		endy = starty + height;
		if (endx >= image.getWidth()) endx = image.getWidth();
		if (endy >= image.getHeight()) endy = image.getHeight();

		for (int y=starty; y<endy; y++)
			for (int x=startx; x<endy; x++) {
				Raycast job = new Raycast();
				rays++;
				Color color = traceRay(job, new Ray(scene.camera.position, scene.camera.getRayDirection(x, y)), 0);
				image.setRGB(x, y, color.toDrawingColor().getRGB());
				
				int depth = (int)(Math.sqrt(job.objdistance)*3.921f); // Depth is limited to 1000 units
				if (depth > 255) 
					depth = 255;
				depth = 255-depth;
				
				depthmap.setRGB(x, y, new java.awt.Color(depth, depth, depth).getRGB());
				lightmap.setRGB(x, y, job.lightColor.toDrawingColor().getRGB());
				
				iterations += job.hits * scene.lights.length * scene.objects.length;
			}
	}
	
	public static boolean isRendering() {
		return rendering;
	}

	public static void drawRenderingThreads(Graphics canvas) {
		canvas.setColor(java.awt.Color.green);
		for (RenderWorker worker : workers)
			if (worker.isAlive())
				canvas.fillRect(worker.getX(), worker.getY(), RenderWorker.width, RenderWorker.height);
	}
}