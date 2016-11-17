package avi.mod.skrim.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import avi.mod.skrim.items.CustomRecord;
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
import net.minecraft.world.storage.loot.conditions.RandomChance;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.event.LootTableLoadEvent;

public class AddTreasure {

	public static RandomValueRange minRolls = new RandomValueRange(1.0F);
	public static RandomValueRange bonusRolls = new RandomValueRange(0.0F, 2.0F);
	public static int maxWeight = 2500;
	public static int currentWeight = 0;
	public static List<LootEntry> lootEntries = new ArrayList<LootEntry>();
	public static List<LootCondition> lootConditions = new ArrayList<LootCondition>();
	public static LootPool skrimPool = null;

	public static int recordWeight = 30;
	public static int recordQuality = 3;
	public static int artifactWeight = 5;
	public static int artifactQuality = 10;

	public static List<ResourceLocation> chests = new ArrayList<ResourceLocation>();
	public static Map<Item, Integer> skrimLoot = new HashMap<Item, Integer>();
	static {
		chests.add(LootTableList.CHESTS_ABANDONED_MINESHAFT);
		chests.add(LootTableList.CHESTS_DESERT_PYRAMID);
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

		for (Entry<String, CustomRecord> entry : ModItems.songs.entrySet()) {
			skrimLoot.put(entry.getValue(), 30);
		}
	}

	public static void addTreasure(LootTableLoadEvent event) {
		if (chests.contains(event.getName())) {
			LootTable table = event.getTable();
			table.addPool(skrimPool);
		}
	}

	/**
	 *
	 * LootEntry(Item, baseWeight, quality, functions, conditions, name)
	 */

	public static void generateSkrimPool() {
		LootCondition[] lootCondition = new LootCondition[0];
		LootFunction[] lootFunction = new LootFunction[0];
		for (Entry<String, CustomRecord> entry : ModItems.songs.entrySet()) {
			lootEntries.add(
				new LootEntryItem(
					entry.getValue(),
					recordWeight,
					recordQuality,
					lootFunction,
					lootCondition,
					entry.getValue().getRecordNameLocal()
				)
			);
			currentWeight += recordWeight;
		}

		for (Item artifact : ModItems.artifacts) {
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
			minRolls,
			bonusRolls,
			"skrimLoot"
		);
	}

}
