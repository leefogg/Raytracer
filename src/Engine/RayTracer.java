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
	private static Scene workingscene;
	public static BufferedImage
	diffusemap,
	depthmap,
	lightmap;
	
	private static RenderWorker[] workers = new RenderWorker[Runtime.getRuntime().availableProcessors()]; //Runtime.getRuntime().availableProcessors()
	private static boolean rendering = false;
	
	public static boolean shadows = true;
	public static final int maxRays = 3;
	public static int iterations, rays;
	
	public static void init(Scene scene, BufferedImage diffuse) {
		workingscene = scene;
		diffusemap = diffuse;
		depthmap = new BufferedImage(diffusemap.getWidth(), diffusemap.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		lightmap = new BufferedImage(diffusemap.getWidth(), diffusemap.getHeight(), BufferedImage.TYPE_INT_RGB);
		
		RenderWorker.updateScene(workingscene);
		RenderWorker.updateImages(diffusemap, depthmap, lightmap);
		RenderWorker.generateRenderPositions();
	}
	
	public void setImageSize(int width, int height) {
		diffusemap = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		depthmap = new BufferedImage(diffusemap.getWidth(), diffusemap.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		lightmap = new BufferedImage(diffusemap.getWidth(), diffusemap.getHeight(), BufferedImage.TYPE_INT_RGB);
		
		RenderWorker.updateImages(diffusemap, depthmap, lightmap);
		RenderWorker.generateRenderPositions();
	}
	
	public static void setScene(Scene scene) {
		workingscene = scene;
		
		RenderWorker.updateScene(workingscene);
	}

	private static RaycastReport intersectScene(Ray ray) {
		float closest = Float.MAX_VALUE;
		RaycastReport closestObject = null;
		for (RenderObject object : workingscene.objects) {
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
	
	public static Color traceRay(Raycast job, Ray ray, int depth) {  //Copy traceRay method for use in viewport that doesn't reccur or shade
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
		for (Light light : workingscene.lights) {
			Vector
			lightDirection = Vector.subtract(light.position, hitpos),
			lightDirectionNorm = Vector.normal(lightDirection);
			
			if (shadows) {
				RaycastReport toLight = intersectScene(new Ray(hitpos, lightDirectionNorm));
				rays++;
				boolean isinshadow = (toLight == null) ? false : (toLight.distance <= Vector.distance(lightDirection));
				if (isinshadow) continue;
			}
			
			float phumbrella = light.getBrightnessAtAngle(hit.ray);
			if (phumbrella == 0) continue;
			
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
		iterations += workingscene.lights.length;
		return outputColor;
	}

	
	public static Thread startRendering() {
		iterations = 0;
		rays = 0;
		
		RenderWorker.reset();
		
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				rendering = true;
				// Initialize all workers
				for (int i=0; i<workers.length; i++)
					workers[i] = new RenderWorker();
				
				// Start them all working
				for (RenderWorker worker : workers)
					worker.start();
				
				// Wait for them all to finish so frame is completely rendered
				for (RenderWorker worker : workers)
					while(worker.isAlive()){}
				
				rendering = false;
			}
		});
		thread.start();
		return thread;
	}

	// Completely impractical to render with a single thread. Only used for debugging.
	public static void render(int startx, int starty, int width, int height) {
		iterations = 0;
		rays = 0;
		
		int 
		endx = startx + width,
		endy = starty + height;
		if (endx >= diffusemap.getWidth()) endx = diffusemap.getWidth();
		if (endy >= diffusemap.getHeight()) endy = diffusemap.getHeight();

		for (int y=starty; y<endy; y++)
			for (int x=startx; x<endy; x++) {
				Raycast job = new Raycast();
				rays++;
				Color color = traceRay(job, new Ray(workingscene.camera.position, workingscene.camera.getRayDirection(x, y)), 0);
				diffusemap.setRGB(x, y, color.toDrawingColor().getRGB());
				
				int depth = (int)(Math.sqrt(job.objdistance)*3.921f); // Depth is limited to 1000 units
				if (depth > 255) depth = 255;
				depth = 255-depth;
				depthmap.setRGB(x, y, new java.awt.Color(depth, depth, depth).getRGB());
				lightmap.setRGB(x, y, job.lightColor.toDrawingColor().getRGB());
				
				iterations += job.hits * workingscene.lights.length * workingscene.objects.length;
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