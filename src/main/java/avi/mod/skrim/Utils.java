package avi.mod.skrim;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;

public class Utils {
	
	public static boolean isPointInRegion(int left, int top, int right, int bottom, int pointX, int pointY) {
		return (pointX > left && pointX < right && pointY > top && pointY < bottom);
	}
	
	public static String snakeCase(String str) {
		return str.toLowerCase().replace(" ", "_");
	}
	
	public static String getBlockName(Block block) {
		return snakeCase(block.getLocalizedName());
	}
	

}
