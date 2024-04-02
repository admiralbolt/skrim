package avi.mod.skrim.world.loot;

import avi.mod.skrim.blocks.SkrimBlocks;
import avi.mod.skrim.items.SkrimItems;
import com.google.common.collect.ImmutableMap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetCount;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Loot pools that dynamically adjust weights to have a total max weight.
 */
public class DynamicLootPool {

  private static final Map<Item, LootFunction[]> FUNCTIONS = ImmutableMap.of(
      SkrimItems.DEATH_ARROW, new LootFunction[]{new SetCount(new LootCondition[0], new RandomValueRange(3, 3))}
  );

  public static LootPool ARTIFACT_POOL = new DynamicLootPool("artifact_pool", 20000, 35, 30, 2, 1, SkrimItems.RAFFLE_TICKET,
      Stream.concat(Arrays.stream(SkrimItems.ARTIFACTS), SkrimBlocks.RegistrationHandler.ARTIFACT_ITEM_BLOCK_MAP.values().stream())).toLootPool();

  public static LootPool HIGH_CHANCE_ARTIFACT_POOL = new DynamicLootPool("high_chance_artifact_pool", 5000, 35, 30, 3, 2, SkrimItems.RAFFLE_TICKET,
      Stream.concat(Arrays.stream(SkrimItems.ARTIFACTS), SkrimBlocks.RegistrationHandler.ARTIFACT_ITEM_BLOCK_MAP.values().stream())).toLootPool();

  public static LootPool RECORD_POOL = new DynamicLootPool("record_pool", 10000, 60, 10, 2, 2, Items.ROTTEN_FLESH,
      Stream.of(SkrimItems.SONGS)).toLootPool();

  private String name;
  private RandomValueRange rolls;
  private RandomValueRange bonusRolls;
  private List<LootEntry> lootEntries = new ArrayList<>();
  private List<LootCondition> lootConditions = new ArrayList<>();


  private DynamicLootPool(String name, int maxWeight, int itemWeight, int itemQuality, int maxRolls, int maxBonusRolls, Item defaultItem,
                          Stream<Item> items) {
    this.name = name;
    this.rolls = new RandomValueRange(1.0f, maxRolls);
    this.bonusRolls = new RandomValueRange(1.0f, maxBonusRolls);
    LootCondition[] lootCondition = new LootCondition[0];
    LootFunction[] lootFunction = new LootFunction[0];
    items.forEach(item -> {
      this.lootEntries.add(new LootEntryItem(item, itemWeight, itemQuality, FUNCTIONS.getOrDefault(item, lootFunction), lootCondition,
          item.getUnlocalizedName()));
    });
    if (lootEntries.size() * itemWeight >= maxWeight) return;
    this.lootEntries.add(new LootEntryItem(defaultItem, maxWeight - lootEntries.size() * itemWeight, -1000, lootFunction,
        lootCondition, defaultItem.getUnlocalizedName()));
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
