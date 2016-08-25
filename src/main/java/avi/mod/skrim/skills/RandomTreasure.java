package avi.mod.skrim.skills;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import avi.mod.skrim.RandomCollection;

public class RandomTreasure {

  private static RandomCollection<Item> treasure = new RandomCollection<Item>();
	static {
		treasure.add(0.2, Items.GOLD_NUGGET);
		treasure.add(0.01, Items.SADDLE);
    treasure.add(0.01, Items.NAME_TAG);
		treasure.add(0.1, Items.FEATHER);
		treasure.add(0.12, Items.GUNPOWDER);
		treasure.add(0.001, Items.DIAMOND_SWORD);
		treasure.add(0.0005, Items.ELYTRA);
		treasure.add(0.002, Items.MAP);
		treasure.add(0.06, Items.ARROW);
		treasure.add(0.01, Items.EMERALD);
		treasure.add(0.12, Items.COAL);
		treasure.add(0.01, Items.EXPERIENCE_BOTTLE);
		treasure.add(0.008, Items.RECORD_11);
		treasure.add(0.008, Items.RECORD_13);
		treasure.add(0.008, Items.RECORD_BLOCKS);
		treasure.add(0.008, Items.RECORD_CAT);
		treasure.add(0.008, Items.RECORD_CHIRP);
		treasure.add(0.008, Items.RECORD_FAR);
		treasure.add(0.008, Items.RECORD_MALL);
		treasure.add(0.008, Items.RECORD_MELLOHI);
		treasure.add(0.008, Items.RECORD_STAL);
		treasure.add(0.008, Items.RECORD_STRAD);
		treasure.add(0.008, Items.RECORD_WAIT);
		treasure.add(0.008, Items.RECORD_WARD);
		treasure.add(0.008,  Items.SPECTRAL_ARROW);
		treasure.add(0.006, Items.DIAMOND_HORSE_ARMOR);
		treasure.add(0.11, Items.POTATO);
		treasure.add(0.1, Items.BOOK);
		treasure.add(0.001, Items.DIAMOND);
		treasure.add(0.0008, Items.NETHER_STAR);
	}

  public static ItemStack generate() {
		return new ItemStack(treasure.next());
	}

}
