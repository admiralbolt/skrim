package avi.mod.skrim.skills;

import avi.mod.skrim.RandomCollection;
import avi.mod.skrim.items.CustomRecord;
import avi.mod.skrim.items.ModItems;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RandomTreasure {

  private static RandomCollection<Item> metalTreasure = new RandomCollection<Item>();
  static {
    metalTreasure.add(0.4, Items.IRON_INGOT);
    metalTreasure.add(0.4, Items.GOLD_INGOT);
    metalTreasure.add(0.05, Items.IRON_LEGGINGS);
    metalTreasure.add(0.05, Items.IRON_AXE);
    metalTreasure.add(0.05, Items.IRON_BOOTS);
    metalTreasure.add(0.05, Items.IRON_CHESTPLATE);
    metalTreasure.add(0.05, Items.IRON_DOOR);
    metalTreasure.add(0.05, Items.IRON_HELMET);
    metalTreasure.add(0.05, Items.IRON_HOE);
    metalTreasure.add(0.05, Items.IRON_HORSE_ARMOR);
    metalTreasure.add(0.05, Items.IRON_PICKAXE);
    metalTreasure.add(0.05, Items.IRON_SHOVEL);
    metalTreasure.add(0.05, Items.IRON_SWORD);
    metalTreasure.add(0.05, Items.GOLDEN_LEGGINGS);
    metalTreasure.add(0.05, Items.GOLDEN_AXE);
    metalTreasure.add(0.05, Items.GOLDEN_BOOTS);
    metalTreasure.add(0.05, Items.GOLDEN_CHESTPLATE);
    metalTreasure.add(0.05, Items.GOLDEN_HELMET);
    metalTreasure.add(0.05, Items.GOLDEN_HOE);
    metalTreasure.add(0.05, Items.GOLDEN_HORSE_ARMOR);
    metalTreasure.add(0.05, Items.GOLDEN_PICKAXE);
    metalTreasure.add(0.05, Items.GOLDEN_SHOVEL);
    metalTreasure.add(0.05, Items.GOLDEN_SWORD);
    metalTreasure.add(0.01, Items.GOLDEN_APPLE);
    metalTreasure.add(0.01, Items.GOLDEN_CARROT);
  }

  private static RandomCollection<Item> treasure = new RandomCollection<Item>();
	static {
		treasure.add(0.08, Items.ARROW);
    treasure.add(0.08, Items.STRING);
    treasure.add(0.04, Items.CLAY_BALL);
    treasure.add(0.01, Items.RABBIT_FOOT);
    treasure.add(0.02, Items.SLIME_BALL);
		treasure.add(0.2, Items.GOLD_NUGGET);
		treasure.add(0.01, Items.SADDLE);
    treasure.add(0.01, Items.NAME_TAG);
		treasure.add(0.1, Items.FEATHER);
		treasure.add(0.12, Items.GUNPOWDER);
		treasure.add(0.001, Items.DIAMOND_SWORD);
		treasure.add(0.0003, Items.ELYTRA);
		treasure.add(0.002, Items.MAP);
		treasure.add(0.06, Items.ARROW);
		treasure.add(0.01, Items.EMERALD);
		treasure.add(0.12, Items.COAL);
		treasure.add(0.01, Items.EXPERIENCE_BOTTLE);
		treasure.add(0.005, Items.RECORD_11);
		treasure.add(0.005, Items.RECORD_13);
		treasure.add(0.005, Items.RECORD_BLOCKS);
		treasure.add(0.005, Items.RECORD_CAT);
		treasure.add(0.005, Items.RECORD_CHIRP);
		treasure.add(0.005, Items.RECORD_FAR);
		treasure.add(0.005, Items.RECORD_MALL);
		treasure.add(0.005, Items.RECORD_MELLOHI);
		treasure.add(0.005, Items.RECORD_STAL);
		treasure.add(0.005, Items.RECORD_STRAD);
		treasure.add(0.005, Items.RECORD_WAIT);
		treasure.add(0.005, Items.RECORD_WARD);
		for (CustomRecord record : ModItems.SONGS.values()) {
			treasure.add(0.001, record);
		}
		treasure.add(0.00,  Items.SPECTRAL_ARROW);
		treasure.add(0.006, Items.DIAMOND_HORSE_ARMOR);
		treasure.add(0.11, Items.POTATO);
		treasure.add(0.1, Items.BOOK);
		treasure.add(0.001, Items.DIAMOND);
		treasure.add(0.0008, Items.NETHER_STAR);
	}

  public static ItemStack generateStandardTreasure() {
		return new ItemStack(treasure.next());
	}

  public static ItemStack generateMetalTreasure() {
  	return new ItemStack(metalTreasure.next());
  }

}
