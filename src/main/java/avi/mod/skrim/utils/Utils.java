package avi.mod.skrim.utils;

import java.text.DecimalFormat;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

public class Utils {

	public static String[] tuplets = {"zero-adsf", "one-asdf", "double", "triple", "quadruple", "quintuple", "sextuple", "septuple", "octople", "nontople", "decuple"};
	public static Random rand = new Random();
	public static DecimalFormat oneDigit = new DecimalFormat("0.0");
	public static DecimalFormat twoDigit = new DecimalFormat("0.00");

	public static boolean isPointInRegion(int left, int top, int right, int bottom, int pointX, int pointY) {
		return (pointX > left && pointX < right && pointY > top && pointY < bottom);
	}

	public static String snakeCase(String str) {
		return str.toLowerCase().replace(" ", "_");
	}

	public static String getBlockName(Block block) {
		return snakeCase(block.getLocalizedName());
	}

	public static void logBlockState(IBlockState state) {
		Block block = state.getBlock();
		System.out.println("harvestTool: " + block.getHarvestTool(state) + ", name: " + getBlockName(block) + ", class: " + block.getClass());
	}

	public static String getFortuneString(int fortuneAmount) {
		return tuplets[fortuneAmount];
	}

	public static int gaussianSum(int n) {
		return (n * n + n) / 2;
	}

	public static String formatPercent(double percent) {
		return (String) oneDigit.format(percent * 100);
	}

	public static String formatPercentTwo(double percent) {
		return (String) twoDigit.format(percent * 100);
	}

}
