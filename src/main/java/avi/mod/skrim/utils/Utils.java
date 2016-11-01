package avi.mod.skrim.utils;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockGravel;
import net.minecraft.block.BlockMelon;
import net.minecraft.block.BlockMycelium;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.BlockRedFlower;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.block.BlockYellowFlower;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.BlockEvent;

public class Utils {

	public static String[] tuplets = { "zero-adsf", "one-asdf", "double", "triple", "quadruple", "quintuple", "sextuple", "septuple", "octople", "nontople", "decuple" };
	public static Random rand = new Random();
	public static DecimalFormat oneDigit = new DecimalFormat("0.0");
	public static DecimalFormat twoDigit = new DecimalFormat("0.00");
	public static HashSet<Potion> negativeEffects = new HashSet<Potion>();
	static {
		negativeEffects.add(MobEffects.BLINDNESS);
		negativeEffects.add(MobEffects.GLOWING);
		negativeEffects.add(MobEffects.HUNGER);
		negativeEffects.add(MobEffects.LEVITATION);
		negativeEffects.add(MobEffects.MINING_FATIGUE);
		negativeEffects.add(MobEffects.NAUSEA);
		negativeEffects.add(MobEffects.POISON);
		negativeEffects.add(MobEffects.SLOWNESS);
		negativeEffects.add(MobEffects.UNLUCK);
		negativeEffects.add(MobEffects.WEAKNESS);
		negativeEffects.add(MobEffects.WITHER);
	}

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
	
	public static void logHurtEvent(LivingHurtEvent event) {
		DamageSource source = event.getSource();
		System.out.println("source.damageType: " + source.damageType + ", damageAmount: " + event.getAmount() + ", isExplosion: " + source.isExplosion() + ", isFire: " + source.isFireDamage() + ", isMagic: " + source.isMagicDamage() + ", isProjectile: " + source.isProjectile());
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
				iattributeinstance.removeModifier(attributemodifier);
				iattributeinstance.applyModifier(new AttributeModifier(attributemodifier.getID(), attributemodifier.getName(), getAttributeModifierAmount(amplifier, attributemodifier), attributemodifier.getOperation()));
			}
		}
	}

	public static double getAttributeModifierAmount(int amplifier, AttributeModifier modifier) {
  	return modifier.getAmount() * (double)(amplifier + 1);
	}

	public static boolean isRawXpBlock(Block block) {
		return (
			block instanceof BlockOldLog ||
			block instanceof BlockNewLog ||
			block instanceof BlockRedFlower ||
			block instanceof BlockYellowFlower ||
			block instanceof BlockSand ||
			block instanceof BlockGravel ||
			block instanceof BlockDirt ||
			block instanceof BlockMycelium ||
			block instanceof BlockGrass ||
			block instanceof BlockSoulSand ||
			block instanceof BlockPumpkin ||
			block instanceof BlockMelon ||
			block instanceof BlockDoublePlant ||
			block == Blocks.IRON_ORE ||
			block == Blocks.GOLD_ORE ||
			block == Blocks.DIAMOND_ORE ||
			block == Blocks.LAPIS_ORE ||
			block == Blocks.REDSTONE_ORE ||
			block == Blocks.COAL_ORE ||
			block == Blocks.EMERALD_ORE
		);
	}

	public static boolean isSilkTouching(BlockEvent.BreakEvent event) {
		EntityPlayer player = event.getPlayer();
		ItemStack mainStack = player.getHeldItemMainhand();
		int silkTouch = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, mainStack);
		return silkTouch > 0;
	}
	
	public static boolean isNegativeEffect(PotionEffect effect) {
		Potion potion = (Potion) Reflection.getPrivateField(effect, "potion");
		return (potion != null && negativeEffects.contains(potion));
	}
}
