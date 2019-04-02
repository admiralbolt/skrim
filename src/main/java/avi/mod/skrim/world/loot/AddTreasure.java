package avi.mod.skrim.world.loot;

import java.util.ArrayList;
import java.util.List;

import avi.mod.skrim.items.items.CustomRecord;
import avi.mod.skrim.items.ModItems;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.event.LootTableLoadEvent;

public class AddTreasure {

	public static RandomValueRange rolls = new RandomValueRange(1.0F, 3.0F);
	public static RandomValueRange bonusRolls = new RandomValueRange(1.0F, 2.0F);
	public static int maxWeight = 2500;
	public static int currentWeight = 0;
	public static List<LootEntry> lootEntries = new ArrayList<LootEntry>();
	public static List<LootCondition> lootConditions = new ArrayList<LootCondition>();
	public static LootPool skrimPool = null;

	public static int recordWeight = 30;
	public static int recordQuality = 3;
	public static int artifactWeight = 4;
	public static int artifactQuality = 10;

	public static List<ResourceLocation> chests = new ArrayList<ResourceLocation>();
	static {
		chests.add(LootTableList.CHESTS_ABANDONED_MINESHAFT);
		chests.add(LootTableList.CHESTS_END_CITY_TREASURE);
		chests.add(LootTableList.CHESTS_IGLOO_CHEST);
		chests.add(LootTableList.CHESTS_JUNGLE_TEMPLE);
		chests.add(LootTableList.CHESTS_JUNGLE_TEMPLE_DISPENSER);
		chests.add(LootTableList.CHESTS_NETHER_BRIDGE);
		chests.add(LootTableList.CHESTS_SIMPLE_DUNGEON);
		chests.add(LootTableList.CHESTS_STRONGHOLD_CORRIDOR);
		chests.add(LootTableList.CHESTS_STRONGHOLD_CROSSING);
		chests.add(LootTableList.CHESTS_STRONGHOLD_LIBRARY);
		chests.add(LootTableList.CHESTS_VILLAGE_BLACKSMITH);
		chests.add(LootTableList.CHESTS_WOODLAND_MANSION);
	}

	public static void addTreasure(LootTableLoadEvent event) {
		if (chests.contains(event.getName())) {
			LootTable table = event.getTable();
			table.addPool(skrimPool);
		}
	}

	/**
	 *
	 * LootEntry(Item, baseWeight, quality, functions, conditions, NAME)
	 */

	public static void generateSkrimPool() {
		LootCondition[] lootCondition = new LootCondition[0];
		LootFunction[] lootFunction = new LootFunction[0];
		for (CustomRecord record : ModItems.SONGS) {
			lootEntries.add(
				new LootEntryItem(
					record,
					recordWeight,
					recordQuality,
					lootFunction,
					lootCondition,
					record.getRecordNameLocal()
				)
			);
			currentWeight += recordWeight;
		}

		for (Item artifact : ModItems.ARTIFACTS) {
			lootEntries.add(
					new LootEntryItem(
						artifact,
						artifactWeight,
						artifactQuality,
						lootFunction,
						lootCondition,
						artifact.getUnlocalizedName()
					)
				);
				currentWeight += artifactWeight;
		}

		if (currentWeight < maxWeight) {
			lootEntries.add(
				new LootEntryItem(
					Items.ROTTEN_FLESH,
					maxWeight - currentWeight,
					-1,
					lootFunction,
					lootCondition,
					"minecraft:rotten_flesh"
				)
			);
		}

		LootEntry[] arrayEntries = new LootEntry[lootEntries.size()];
		LootCondition[] arrayConditions = new LootCondition[lootConditions.size()];
		lootEntries.toArray(arrayEntries);
		lootConditions.toArray(arrayConditions);
		skrimPool = new LootPool(
			arrayEntries,
			arrayConditions,
			rolls,
			bonusRolls,
			"skrimLoot"
		);
	}

}
