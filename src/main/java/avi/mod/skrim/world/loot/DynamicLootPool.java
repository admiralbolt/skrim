package avi.mod.skrim.world.loot;

import avi.mod.skrim.blocks.SkrimBlocks;
import avi.mod.skrim.items.SkrimItems;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Loot pools that dynamically adjust weights to have a total max weight.
 */
public class DynamicLootPool {

  public static LootPool ARTIFACT_POOL = new DynamicLootPool("artifact_pool", 10000, 10, 25, 3, 2,
      Stream.concat(Arrays.stream(SkrimItems.ARTIFACTS), SkrimBlocks.RegistrationHandler.ARTIFACT_ITEM_BLOCK_MAP.values().stream())).toLootPool();

  public static LootPool RECORD_POOL = new DynamicLootPool("record_pool", 10000, 50, 10, 2, 2,
      Stream.of(SkrimItems.SONGS)).toLootPool();


  private String name;
  private RandomValueRange rolls;
  private RandomValueRange bonusRolls;
  private List<LootEntry> lootEntries = new ArrayList<>();
  private List<LootCondition> lootConditions = new ArrayList<>();



  private DynamicLootPool(String name, int maxWeight, int itemWeight, int itemQuality, int maxRolls, int maxBonusRolls,
                          Stream<Item> items) {
    this.name = name;
    this.rolls = new RandomValueRange(1.0f, maxRolls);
    this.bonusRolls = new RandomValueRange(1.0f, maxBonusRolls);
    LootCondition[] lootCondition = new LootCondition[0];
    LootFunction[] lootFunction = new LootFunction[0];
    items.forEach(item -> {
      this.lootEntries.add(new LootEntryItem(item, itemWeight, itemQuality, lootFunction, lootCondition, item.getUnlocalizedName()));
    });
    if (lootEntries.size() * itemWeight >= maxWeight) return;
    this.lootEntries.add(new LootEntryItem(Items.ROTTEN_FLESH, maxWeight - lootEntries.size() * itemWeight, -1, lootFunction,
        lootCondition, "minecraft:rotten_flesh"));
  }

  private LootPool toLootPool() {
    LootEntry[] arrayEntries = new LootEntry[lootEntries.size()];
    LootCondition[] arrayConditions = new LootCondition[lootConditions.size()];
    lootEntries.toArray(arrayEntries);
    lootConditions.toArray(arrayConditions);
    return new LootPool(
        arrayEntries,
        arrayConditions,
        rolls,
        bonusRolls,
        this.name
    );
  }

}
