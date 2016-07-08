package Utils;

import java.util.ArrayList;

import Engine.Scene;
import Objects.Light;
import Objects.RenderObject;

public final class Subdivmap {
	int X, Y, Z, Width, Height, Depth;
	
	Voxel[][][] map;
	
	private class Voxel {
		private final static int Size = 5;
		ArrayList<RenderObject> objects = new ArrayList<RenderObject>();
		ArrayList<Light> lights = new ArrayList<Light>();
	}
	
	public Subdivmap(Scene scene) {
		long before = System.nanoTime();
		// Find scene bounding box
		float 
		minx = Float.MAX_VALUE, 
		miny = Float.MAX_VALUE, 
		minz = Float.MAX_VALUE, 
		maxx = Float.MIN_VALUE, 
		maxy = Float.MIN_VALUE, 
		maxz = Float.MIN_VALUE;
		for (RenderObject obj : scene.objects) {
			if (obj.position.x-obj.halfsize.x < minx) minx = obj.position.x-obj.halfsize.x;
			if (obj.position.y-obj.halfsize.y < miny) miny = obj.position.y-obj.halfsize.y;
			if (obj.position.z-obj.halfsize.z < minz) minz = obj.position.z-obj.halfsize.z;
			if (obj.position.x+obj.halfsize.x > maxx) maxx = obj.position.x+obj.halfsize.x;
			if (obj.position.y+obj.halfsize.y > maxy) maxy = obj.position.y+obj.halfsize.y;
			if (obj.position.z+obj.halfsize.z > maxz) maxz = obj.position.z+obj.halfsize.z;
		}
		for (Light light : scene.lights) {
			if (light.position.x < minx) minx = light.position.x;
			if (light.position.y < miny) miny = light.position.y;
			if (light.position.z < minz) minz = light.position.z;
			if (light.position.x > maxx) maxx = light.position.x;
			if (light.position.y > maxy) maxy = light.position.y;
			if (light.position.z > maxz) maxz = light.position.z;
		}
		if (scene.camera.position.x < minx) minx = scene.camera.position.x;
		if (scene.camera.position.y < miny) miny = scene.camera.position.y;
		if (scene.camera.position.z < minz) minz = scene.camera.position.z;
		if (scene.camera.position.x > maxx) maxx = scene.camera.position.x;
		if (scene.camera.position.y > maxy) maxy = scene.camera.position.y;
		if (scene.camera.position.z > maxz) maxz = scene.camera.position.z;
		
//		System.out.println("minx: " + minx);
//		System.out.println("miny: " + miny);
//		System.out.println("minz: " + minz);
//		System.out.println("maxx: " + maxx);
//		System.out.println("maxy: " + maxy);
//		System.out.println("maxz: " + maxz);
//		System.out.println();
		// Round to multiples of voxel size but still contain box
		if (minx >= 0) minx -= minx % Voxel.Size; else minx += -Voxel.Size-(minx % Voxel.Size);
		if (miny >= 0) miny -= miny % Voxel.Size; else miny += -Voxel.Size-(miny % Voxel.Size);
		if (minz >= 0) minz -= minz % Voxel.Size; else minz += -Voxel.Size-(minz % Voxel.Size);
		maxx += Voxel.Size-(maxx % Voxel.Size);
		maxy += Voxel.Size-(maxy % Voxel.Size);
		maxz += Voxel.Size-(maxz % Voxel.Size);
//		System.out.println("minx: " + minx);
//		System.out.println("miny: " + miny);
//		System.out.println("minz: " + minz);
//		System.out.println("maxx: " + maxx);
//		System.out.println("maxy: " + maxy);
//		System.out.println("maxz: " + maxz);
		
		
		
		X = (int)minx;
		Y = (int)miny;
		Z = (int)minz;
		X /= Voxel.Size;
		Y /= Voxel.Size;
		Z /= Voxel.Size;
		Width = (int)(maxx - minx)/Voxel.Size;
		Height= (int)(maxy - miny)/Voxel.Size;
		Depth = (int)(maxz - minz)/Voxel.Size;
		
		map = new Voxel[Width][Height][Depth];
		for (int z=0; z<Depth; z++)
			for (int y=0; y<Height; y++)
				for (int x=0; x<Width; x++)
					map[x][y][z] = new Voxel();
		
		for (RenderObject obj : scene.objects) {
			minx = obj.position.x-obj.halfsize.x;
			miny = obj.position.y-obj.halfsize.y;
			minz = obj.position.z-obj.halfsize.z;
			maxx = obj.position.x+obj.halfsize.x;
			maxy = obj.position.y+obj.halfsize.y;
			maxz = obj.position.z+obj.halfsize.z;
			
			for (; minx<maxx; minx+=Voxel.Size)
				for (; miny<maxy; miny+=Voxel.Size)
					for (; minz<maxz; minz+=Voxel.Size)
						getObjects(minx, miny, minz).add(obj);
		}
		for (Light light : scene.lights) 
			getLights(light.position.x, light.position.y, light.position.z).add(light);
	}
	
	public ArrayList<RenderObject> getObjects(float x, float y, float z) {
		x /= Voxel.Size;
		y /= Voxel.Size;
		z /= Voxel.Size;
		x -= X;
		y -= Y;
		z -= Z;
//		if (x < X) return null;
//		if (y < Y) return null;
//		if (z < Z) return null;
//		if (x > Width) return null;
//		if (y > Height) return null;
//		if (z > Depth) return null;
		return map[(int)x][(int)y][(int)z].objects;
	}
	public ArrayList<Light> getLights(float x, float y, float z) {
		x -= X;
		y -= Y;
		z -= Z;
//		if (x < X) return null;
//		if (y < Y) return null;
//		if (z < Z) return null;
		x /= Voxel.Size;
		y /= Voxel.Size;
		z /= Voxel.Size;
//		if (x > Width) return null;
//		if (y > Height) return null;
//		if (z > Depth) return null;
		return map[(int)x][(int)y][(int)z].lights;
	}
	
	public boolean isInBounds(float x, float y, float z) {
		x /= Voxel.Size;
		y /= Voxel.Size;
		z /= Voxel.Size;
		x -= X;
		y -= Y;
		z -= Z;
		if (x < X) return false;
		if (y < Y) return false;
		if (z < Z) return false;
		if (x >= X+Width) return false;
		if (y >= Y+Height) return false;
		if (z >= Z+Depth) return false;
		return true;
	}
	
	@Override
	public String toString() {
		return 	"X: " + X
			+	"\nY: " + Y
			+	"\nZ: " + Z
			+	"\nWidth: " + Width
			+	"\nHeight: " + Height
			+	"\nDepth: " + Depth;
	}
}