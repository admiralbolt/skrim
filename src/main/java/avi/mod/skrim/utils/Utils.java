package avi.mod.skrim.utils;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;

public class Utils {

	public static String[] tuplets = { "zero-adsf", "one-asdf", "double", "triple", "quadruple", "quintuple", "sextuple", "septuple", "octople", "nontople", "decuple" };
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

	// registerPotionAttributeModifier(SharedMonsterAttributes.MAX_HEALTH, "5D6F0BA2-1186-46AC-B896-C61C5CEE99CC", 4.0D, 0).setBeneficial());

	private final Map<IAttribute, AttributeModifier> attributeModifierMap = Maps.<IAttribute, AttributeModifier>newHashMap();

	public static void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBaseIn, Map<IAttribute, AttributeModifier> attributeMap, int amplifier) {
		AbstractAttributeMap entityAttributes = entityLivingBaseIn.getAttributeMap();
		for (Entry<IAttribute, AttributeModifier> entry : attributeMap.entrySet()) {
			IAttributeInstance iattributeinstance = entityAttributes.getAttributeInstance((IAttribute) entry.getKey());
			if (iattributeinstance != null) {
				AttributeModifier attributemodifier = (AttributeModifier) entry.getValue();
				System.out.println("remove then apply, name -> " + attributemodifier.getName());
				iattributeinstance.removeModifier(attributemodifier);
				iattributeinstance.applyModifier(new AttributeModifier(attributemodifier.getID(), attributemodifier.getName(), getAttributeModifierAmount(amplifier, attributemodifier), attributemodifier.getOperation()));
			}
		}
	}

	public static double getAttributeModifierAmount(int amplifier, AttributeModifier modifier) {
  	return modifier.getAmount() * (double)(amplifier + 1);
	}

}
