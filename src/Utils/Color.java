package Utils;
public final class Color {
	public static final Color
	white 	= new Color(1, 1, 1),
	grey 	= new Color(.5f, .5f, .5f),
	black	= new Color(0, 0, 0),
	purple 	= new Color(1f,0,1f), 
	voidColor = black,
	defaultcolor = black; 
	
	float red, green, blue;
	
	public Color() {
		
	}
	
	public Color(float red, float green, float blue) {
		set(red, green, blue);
	}
	
	public void set(float red, float green, float blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public static Color scale(float scale, Color c) {
		return new Color(scale * c.red, scale * c.green, scale * c.blue);
	}
	
	public void scale(Color other) {
		red *= other.red;
		blue *= other.green;
		green *= other.blue;
	}
	
	public void scale(float scale) {
		red *= scale;
		blue *= scale;
		green *= scale;
	}
	
	public void add(Color other) {
		red += other.red;
		green += other.green;
		blue += other.blue;
	}
	
	public static Color add(Color c1, Color c2) {
		return new Color(c1.red + c2.red, c1.green + c2.green, c1.blue + c2.blue);
	}
	
	public static Color multiply(Color c1, Color c2) {
		return new Color(c1.red * c2.red, c1.green * c2.green, c1.blue * c2.blue);
	}
	
	public void multiply(Color other) {
		red *= other.red;
		blue *= other.green;
		green *= other.blue;
	}
	
	public Color Clone() {
		return new Color(red, green, blue);
	}
	
	public int getRGB() {
		int 
		r = (int)(red*255),
		g = (int)(green*255),
		b = (int)(blue*255);
		if (r < 0) r = 0;
		if (g < 0) g = 0;
		if (b < 0) b = 0;
		if (r >= 255) r = 255;
		if (g >= 255) g = 255;
		if (b >= 255) b = 255;
		return r | (g << 8) | (b << 16);
	}
	
	public java.awt.Color toDrawingColor() {
		float 
		r = red,
		g = green,
		b = blue;
		if (r < 0) r = 0;
		if (g < 0) g = 0;
		if (b < 0) b = 0;
		if (r >= 1f) r = 1;
		if (g >= 1f) g = 1;
		if (b >= 1f) b = 1;
		return new java.awt.Color(r, g, b);
	}
}