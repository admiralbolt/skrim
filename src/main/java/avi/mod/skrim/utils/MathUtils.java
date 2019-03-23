package avi.mod.skrim.utils;

public class MathUtils {
	
	public static double linearRescale(double currentValue, double min, double max, double newMin, double newMax) {
		double currentRescale = (currentValue - min) / (max - min);
		return newMin * (1 - currentRescale) + newMax * currentRescale;
	}

}
