package Utils;


public class Framecounter {
	public int FPS = 60;
	public float delta = 1000/FPS;
	private long lastFrame = System.nanoTime();
	private int fpstemp = 0;
	private float tickdelay;
	
	public boolean hasDoneFrames(int number) {
		return fpstemp >= number;
	}
	
	public void newframe() {
		fpstemp++;
		
		long currenttime = System.nanoTime();
		delta = (int)(currenttime - lastFrame)/1000000f;
		tickdelay+=delta;
		
		lastFrame = currenttime;
		
		if (tickdelay >= 1000) {
			tickdelay %= 1000;
			FPS = fpstemp;
			fpstemp=0;
		}
	}
}