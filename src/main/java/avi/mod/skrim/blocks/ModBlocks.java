package avi.mod.skrim.blocks;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;
import avi.mod.skrim.Skrim;
import avi.mod.skrim.items.ItemModelProvider;

public final class ModBlocks {

  public static Block orePenguin;

  public static void createBlocks() {
    orePenguin = register(new BlockOre("orePenguin").setCreativeTab(CreativeTabs.MATERIALS));
  }

  private static <T extends Block> T register (T block, ItemBlock itemBlock) {
    GameRegistry.register(block);
    if (itemBlock != null) {
      GameRegistry.register(itemBlock);
    }

    if (block instanceof ItemModelProvider) {
      ((ItemModelProvider) block).registerItemModel(itemBlock);
    }

    return block;
  }

  private static <T extends Block> T register(T block) {
    ItemBlock itemBlock = new ItemBlock(block);
    itemBlock.setRegistryName(block.getRegistryName());
    return register(block, itemBlock);
  }

}
