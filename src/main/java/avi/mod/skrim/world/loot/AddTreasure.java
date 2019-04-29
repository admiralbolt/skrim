package avi.mod.skrim.world.loot;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.event.LootTableLoadEvent;

import java.util.List;

public class AddTreasure {

  private static List<ResourceLocation> LOOT_TABLES = ImmutableList.of(
      LootTableList.CHESTS_ABANDONED_MINESHAFT,
      LootTableList.CHESTS_END_CITY_TREASURE,
      LootTableList.CHESTS_IGLOO_CHEST,
      LootTableList.CHESTS_DESERT_PYRAMID,
      LootTableList.CHESTS_JUNGLE_TEMPLE,
      LootTableList.CHESTS_JUNGLE_TEMPLE_DISPENSER,
      LootTableList.CHESTS_NETHER_BRIDGE,
      LootTableList.CHESTS_SIMPLE_DUNGEON,
      LootTableList.CHESTS_SPAWN_BONUS_CHEST,
      LootTableList.CHESTS_STRONGHOLD_CORRIDOR,
      LootTableList.CHESTS_STRONGHOLD_CROSSING,
      LootTableList.CHESTS_STRONGHOLD_LIBRARY,
      LootTableList.CHESTS_VILLAGE_BLACKSMITH,
      LootTableList.CHESTS_WOODLAND_MANSION,
      LootTableList.ENTITIES_ENDER_DRAGON
  );



  public static void addTreasure(LootTableLoadEvent event) {
    if (!LOOT_TABLES.contains(event.getName())) return;

    event.getTable().addPool(DynamicLootPool.ARTIFACT_POOL);
    event.getTable().addPool(DynamicLootPool.RECORD_POOL);
  }

}
