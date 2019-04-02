package avi.mod.skrim.client.model;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.blocks.BlockRegistrationUtils;
import avi.mod.skrim.blocks.ModBlocks;
import net.minecraft.block.Block;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Registers all models for skrim blocks. Interesting logic is located in BlockRegistrationUtils, list of blocks is
 * located in ModBlocks.
 */
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = Skrim.MOD_ID)
public class ModelManager {

  @SubscribeEvent
  public static void registerModels(final ModelRegistryEvent event) {
    for (Block block : ModBlocks.RegistrationHandler.ALL_BLOCKS) {
      BlockRegistrationUtils.registerBlockItemModel(block.getDefaultState());
    }
  }


}
