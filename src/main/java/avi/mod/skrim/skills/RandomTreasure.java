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

	public static ItemStack generateMetalTreasure() {
		return new ItemStack(metalTreasure.next());
	}

}
