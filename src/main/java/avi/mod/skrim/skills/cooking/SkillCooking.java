package avi.mod.skrim.skills.cooking;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import avi.mod.skrim.Utils;
import avi.mod.skrim.items.CustomFood;
import avi.mod.skrim.items.ModItems;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillStorage;

public class SkillCooking extends Skill implements ISkillCooking {

	public static SkillStorage<ISkillCooking> skillStorage = new SkillStorage<ISkillCooking>();
	public static Map<String, Integer> xpMap;
	public static Map<String, CustomFood> foodMap;
	public int lastItemNumber;

	private static void addFood(String name, CustomFood food, int xp) {
		foodMap.put(name, food);
		xpMap.put(name, xp);
	}

	static {
		foodMap = new HashMap<String, CustomFood>();
		xpMap = new HashMap<String, Integer>();
		addFood("bread", ModItems.overwriteBread, 20);
		addFood("cooked_fish", ModItems.overwriteFish, 30);
		addFood("cooked_salmon", ModItems.overwriteSalmon, 30);
		addFood("beefcooked", ModItems.overwriteSteak, 20);
		addFood("porkchopcooked", ModItems.overwritePorkchop, 20);
		addFood("chickencooked", ModItems.overwriteChicken, 20);
		addFood("rabbitcooked", ModItems.overwriteRabbit, 50);
		addFood("potatobaked", ModItems.overwriteBakedPotato, 10);
		addFood("muttoncooked", ModItems.overwriteMutton, 20);
		addFood("beetroot_soup", ModItems.overwriteBeetrootSoup, 100);
		addFood("mushroomstew", ModItems.overwriteMushroomStew, 100);
		addFood("rabbitstew", ModItems.overwriteRabbitStew, 250);
		addFood("pumpkinpie", ModItems.overwritePumpkinPie, 150);
		addFood("cookie", ModItems.overwriteCookie, 15);
	}

	public SkillCooking() {
		this(1, 0);
	}

	public SkillCooking(int level, int currentXp) {
		super("Cooking", level, currentXp);
		this.iconTexture = new ResourceLocation("skrim", "textures/guis/skills/cooking.png");
	}

	public CustomFood getOverwriteFood(String name) {
		return (foodMap.containsKey(name)) ? foodMap.get(name) : null;
	}

	public int getXp(String blockName) {
		return (xpMap.containsKey(blockName)) ? xpMap.get(blockName) : 0;
	}

	/**
	 * These methods are static so they can be accessed from the CustomFood
	 * class onFoodEaten() method.
	 */

	public static double extraFood(int level) {
		return 0.02 * level;
	}

	public static double extraSaturation(int level) {
		return 0.04 * level;
	}

	public static boolean overFull(int level) {
		return (level >= 25);
	}

	@Override
	public List<String> getToolTip() {
		DecimalFormat fmt = new DecimalFormat("0.0");
		List<String> tooltip = new ArrayList<String>();
		tooltip.add("Your cooking provides §a+" + fmt.format(extraFood(this.level) * 100) + "%§r food.");
		tooltip.add("Your cooking provides §a+" + fmt.format(extraSaturation(this.level) * 100) + "%§r saturation");
		tooltip.add("Shift clicking crafted items provides §aregular and modded§r food.");
		tooltip.add("§eWe swear this is a feature and not a bug...§r");
		if (overFull(this.level)) {
			tooltip.add("Your cooking §aignores food and saturation caps§r.");
		}
		return tooltip;
	}

  public boolean validCookingTarget(ItemStack stack) {
		Item item = stack.getItem();
		return (item instanceof ItemFood
			|| item instanceof ItemFishFood
		) ? true : false;
  }

  public String getFoodName(ItemStack stack) {
  	Item item = stack.getItem();
  	if (item instanceof ItemFishFood) {
  		ItemFishFood fish = (ItemFishFood) item;
  		ItemFishFood.FishType type = ItemFishFood.FishType.byItemStack(stack);
  		if (type == ItemFishFood.FishType.SALMON) {
  			return "cooked_salmon";
  		} else if (type == ItemFishFood.FishType.COD) {
  			return "cooked_fish";
  		} else {
  			return null;
  		}
  	} else if (item instanceof ItemFood) {
  		return Utils.snakeCase(item.getUnlocalizedName()).substring(5);
  	} else {
  		return null;
  	}
  }

}
