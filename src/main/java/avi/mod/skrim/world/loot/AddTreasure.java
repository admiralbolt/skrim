package avi.mod.skrim.world.loot;

import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.event.LootTableLoadEvent;

import java.util.Arrays;
import java.util.List;

public class AddTreasure {

  public static void addTreasure(LootTableLoadEvent event) {
    List<String> paths = Arrays.asList(CustomLootTables.CHESTS_BEANSTALK.getResourcePath(),
            CustomLootTables.DESERT_TEMPLE.getResourcePath(),
            CustomLootTables.METAL_TREASURE.getResourcePath(),
            CustomLootTables.RANDOM_TREASURE.getResourcePath());
    String path = event.getName().getResourcePath();
    if (paths.contains(path) || !path.startsWith("chest")) return;

    event.getTable().addPool(DynamicLootPool.RECORD_POOL);

    // Higher chance for artifacts to spawn in the bonus chest. Will use this to make lament configurum better.
    if (event.getName() == LootTableList.CHESTS_SPAWN_BONUS_CHEST) {
      event.getTable().addPool(DynamicLootPool.HIGH_CHANCE_ARTIFACT_POOL);
    } else {
      event.getTable().addPool(DynamicLootPool.ARTIFACT_POOL);
    }
  }

}
