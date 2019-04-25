package avi.mod.skrim.world.loot;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;

import java.util.List;

public class CustomLootTables {

  public static ResourceLocation CHESTS_BEANSTALK = null;
  public static ResourceLocation RANDOM_TREASURE = null;
  public static ResourceLocation METAL_TREASURE = null;
  // This is a duplicate of the vanilla desert temple that WON'T have artifacts added to it.
  public static ResourceLocation DESERT_TEMPLE = null;

  public static LootTable BEANSTALK_TABLE = null;

  private static ResourceLocation register(String name) {
    return LootTableList.register(new ResourceLocation(Skrim.MOD_ID, name));
  }

  public static void registerLootTables() {
    CHESTS_BEANSTALK = register("chests/beanstalk");
    RANDOM_TREASURE = register("gameplay/random_treasure");
    METAL_TREASURE = register("gameplay/metal_treasure");
    DESERT_TEMPLE = register("chests/desert_temple");
  }

  public static ItemStack getRandomTreasure(World world, EntityPlayer player, int level) {
    LootContext.Builder builder = new LootContext.Builder((WorldServer) world);
    builder.withLuck(player.getLuck() + (float) (level / 5));
    List<ItemStack> items = world.getLootTableManager().getLootTableFromLocation(RANDOM_TREASURE).generateLootForPools(Utils.rand,
				builder.build());
    return items.get(0);
  }

  public static ItemStack getMetalTreasure(World world, EntityPlayer player, int level) {
    LootContext.Builder builder = new LootContext.Builder((WorldServer) world);
    builder.withLuck(player.getLuck() + (float) (level / 5));
    List<ItemStack> items = world.getLootTableManager().getLootTableFromLocation(METAL_TREASURE).generateLootForPools(Utils.rand,
				builder.build());
    return items.get(0);
  }

}
