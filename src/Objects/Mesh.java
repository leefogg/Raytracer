package Objects;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import Engine.Ray;
import Engine.RaycastReport;
import Materials.Material;
import Utils.Vector;

public final class Mesh extends RenderObject {
	
	private class Triangle extends RenderObject {
		int A, B, C;
		Vector edge1, edge2, normal;

		public Triangle(int a, int b, int c, Material mat) {
			super(mat);
			A = a;
			B = b;
			C = c;
			
			edge1 = Vector.subtract(B(), A());
			edge2 = Vector.subtract(C(), A());
			
			updateNormal();
		}
		
		private Vector A() {
			return verts.get(A);
		}
		private Vector B() {
			return verts.get(B);
		}
		private Vector C() {
			return verts.get(C);
		}
		
		public void updateNormal() {
			normal = getNormal(null);
		}

		@Override
		public Vector getNormal(Vector pos) {
			return Vector.cross(edge2, edge1).normalize();
		}
		
		@Override
		public void intersect(RaycastReport report, Ray ray) {
			//if (Vector.dot(ray.direction, normal) < 0) return null;
			/*
			float trisize = normal.dot(A());
			float a = ray.direction.dot(normal);
			//if (a < -0.1f && a >= 0.1f) return null; // Ray is parallel
			float b = normal.dot(Vector.scale(trisize, normal).negate().add(ray.origin));
			float distance2plane = -1*b/a;
			
			Vector Q = ray.direction.Clone().multiply(distance2plane).add(ray.origin);
			Vector QA = Vector.subtract(Q, A());
			if (Vector.cross(edge2, QA).dot(normal) < 0) return null;
			Vector BC = Vector.subtract(B(), C());
			Vector QC = Vector.subtract(Q, C());
			if (Vector.cross(BC, QC).dot(normal) < 0) return null;
			Vector AB = Vector.subtract(A(), B());
			Vector QB = Vector.subtract(Q, B());
			if (Vector.cross(AB, QB).dot(normal) < 0) return null;
			*/
			float d, inv_d, u, v, t;
			Vector P = Vector.cross(ray.direction, edge2);
			d = edge1.dot(P);
			if (d < 1e-3)
				return;
			inv_d = 1f / d;
			
			Vector T = Vector.subtract(ray.origin, A());
			u = T.dot(P) * inv_d;
			if (u < 0f || u > 1f)
				return;
			
			Vector Q = Vector.cross(T, edge1);
			v = ray.direction.dot(Q) * inv_d;
			if (v < 0f || u + v > 1f)
				return;
			
			t = edge2.dot(Q) * inv_d;
			if (t <= 1e-3)
				return;
			
			report.check(this, ray, t);
		}
		
		public Triangle Clone() {
			return new Triangle(A,B,C,material);
		}

	}
	
	private ArrayList<Triangle> faces = new ArrayList<Triangle>();
	private ArrayList<Vector> verts = new ArrayList<Vector>();
	
	public Mesh() {
		super();
	}
	
	public Mesh(Material mat) {
		super(mat);
	}

	public Mesh(String objpath, Material mat) throws IOException {
		super(mat);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(objpath))));
		String line;
		while ((line = br.readLine()) != null)  {
			if (line.startsWith("v ")) {
				String[] positions = line.substring(2).split(" ");
				verts.add(new Vector(
					Float.valueOf(positions[0]),
					Float.valueOf(positions[1]),
					Float.valueOf(positions[2])
				));
			} else if (line.startsWith("f ")) {
				String[] components = line.substring(2).split(" ");
				ArrayList<Integer> indicies = new ArrayList<Integer>();
				for (String component : components) 
					indicies.add(Integer.valueOf(component.split("/")[0])-1);
				faces.add(new Triangle(indicies.get(0), indicies.get(1), indicies.get(2), mat));
			}
		}
		br.close();
		
//		System.out.println("Finished loading .obj file " + objpath.substring(objpath.lastIndexOf("/")+1) + ".");
//		System.out.println(faces.size() + " faces.");
//		System.out.println(verts.size() + " verts.");
	}
	
	public void translate(float x, float y, float z) {
		translate(new Vector(x,y,z));
	}
	public void translate(Vector translation) {
		for (Vector v : verts)
			v.add(translation);
		
		updateFaceNormals();
	}
	
	public void rotate(float x, float y, float z) {
		for (int i=0; i<verts.size(); i++)
			verts.set(i, verts.get(i).rotate(x, y, z));
	}
	public void rotateX(float deg) {
		for (int i=0; i<verts.size(); i++)
			verts.set(i, verts.get(i).rotateX(deg));
		
		updateFaceNormals();
	}
	public void rotateY(float deg) {
		for (int i=0; i<verts.size(); i++)
			verts.set(i, verts.get(i).rotateY(deg));
		
		updateFaceNormals();
	}
	public void rotateZ(float deg) {
		for (int i=0; i<verts.size(); i++)
			verts.set(i, verts.get(i).rotateZ(deg));
		
		updateFaceNormals();
	}
	
	public void scale(float x, float y, float z) {
		scale(new Vector(x,y,z));
	}
	public void scale(Vector scaler) {
		for (Vector vert : verts)
			vert.multiply(scaler);
	}
	
	public void merge(Mesh mesh) {
		for (Vector vert : mesh.verts)
			verts.add(vert.Clone());
		for (Triangle face : mesh.faces)
			faces.add(face.Clone());
		System.out.println(verts.size() + " verts");
		System.out.println(faces.size() + " faces");
	}
	
	public void updateFaceNormals() {
		for (Triangle face : faces) 
			face.updateNormal();
	}

	@Override
	public Vector getNormal(Vector pos) {
		return Vector.subtract(position, pos);
	}

	@Override
	public void intersect(RaycastReport report, Ray ray) {
		for (Triangle tri : faces)
			tri.intersect(report, ray);
	}
	
	public static Mesh createQuad(Material mat) {
		Mesh mesh = new Mesh(mat);
	
		mesh.verts.add(new Vector(-0.5f, -0.5f, 0f));
		mesh.verts.add(new Vector(0.5f, -0.5f, 0f));
		mesh.verts.add(new Vector(0.5f, 0.5f, 0f));
		mesh.verts.add(new Vector(-0.5f, 0.5f, 0f));
		
		mesh.faces.add(mesh.new Triangle(0,1,2,mat));
		mesh.faces.add(mesh.new Triangle(2,3,0,mat));
		
		return mesh;
	}
}