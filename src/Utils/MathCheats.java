package Utils;

public final class MathCheats {
	private static final int 
		resolution = 1 << 22,
		sincosResolution = 12, 
		SIN_MASK = ~(-1 << sincosResolution);

	private static final float[] 
		sqrt = new float[resolution],
		sin,
		cos;
	private static final float
		sqrtmax = 4000,
		sqrtToTable = resolution / sqrtmax,
		radToIndex,
		degToIndex;

	static {
		float inc = sqrtmax / resolution;
		float val = 0;
		for (int i=0; i<resolution; i++) {
			sqrt[i] = (float)Math.sqrt(val);
			val += inc;
		}
		
		int SIN_COUNT = SIN_MASK + 1;
		float radFull    = (float)(Math.PI * 2);
		float degFull    = 360f;
		radToIndex = SIN_COUNT / radFull;
		degToIndex = SIN_COUNT / degFull;
		
		sin = new float[SIN_COUNT];
		cos = new float[SIN_COUNT];
		
		for (int i = 0; i < SIN_COUNT; i++) {
			sin[i] = (float)Math.sin((i + 0.5f) / SIN_COUNT * radFull);
			cos[i] = (float)Math.cos((i + 0.5f) / SIN_COUNT * radFull);
		}
		
		float radtodeg = (float)Math.PI / 180f;
		for (int i = 0; i < 360; i += 90) {
			sin[(int)(i * degToIndex) & SIN_MASK] = (float)Math.sin(i * radtodeg);
			cos[(int)(i * degToIndex) & SIN_MASK] = (float)Math.cos(i * radtodeg);
		}
	}

	public static final float getSqrt(float in) {
		if (in >= sqrtmax) {// If we haven't buffered this 
			return (float)Math.sqrt(in); // Have to do it the slow way
		}
		return sqrt[(int)(in * sqrtToTable)]; // Otherwise we can look it up
	}

	public static final float sin(float rad) {
		return sin[(int)(rad * radToIndex) & SIN_MASK];
	}

	public static final float cos(float rad) {
		return cos[(int)(rad * radToIndex) & SIN_MASK];
	}

	public static final float sinDeg(float deg) {
		return sin[(int)(deg * degToIndex) & SIN_MASK];
	}

	public static final float cosDeg(float deg) {
		return cos[(int)(deg * degToIndex) & SIN_MASK];
	}
}