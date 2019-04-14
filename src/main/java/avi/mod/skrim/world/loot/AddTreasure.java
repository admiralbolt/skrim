package avi.mod.skrim.world.loot;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.event.LootTableLoadEvent;

import java.util.ArrayList;
import java.util.List;

public class AddTreasure {

  public static List<ResourceLocation> chests = new ArrayList<ResourceLocation>();

  static {
    chests.add(LootTableList.CHESTS_ABANDONED_MINESHAFT);
    chests.add(LootTableList.CHESTS_END_CITY_TREASURE);
    chests.add(LootTableList.CHESTS_IGLOO_CHEST);
    chests.add(LootTableList.CHESTS_JUNGLE_TEMPLE);
    chests.add(LootTableList.CHESTS_JUNGLE_TEMPLE_DISPENSER);
    chests.add(LootTableList.CHESTS_NETHER_BRIDGE);
    chests.add(LootTableList.CHESTS_SIMPLE_DUNGEON);
    chests.add(LootTableList.CHESTS_SPAWN_BONUS_CHEST);
    chests.add(LootTableList.CHESTS_STRONGHOLD_CORRIDOR);
    chests.add(LootTableList.CHESTS_STRONGHOLD_CROSSING);
    chests.add(LootTableList.CHESTS_STRONGHOLD_LIBRARY);
    chests.add(LootTableList.CHESTS_VILLAGE_BLACKSMITH);
    chests.add(LootTableList.CHESTS_WOODLAND_MANSION);
  }

  public static void addTreasure(LootTableLoadEvent event) {
    if (!chests.contains(event.getName())) return;
    event.getTable().addPool(DynamicLootPool.ARTIFACT_POOL);
    event.getTable().addPool(DynamicLootPool.RECORD_POOL);
  }

}
