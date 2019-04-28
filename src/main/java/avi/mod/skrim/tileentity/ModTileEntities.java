package avi.mod.skrim.tileentity;

import avi.mod.skrim.Skrim;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber(modid = Skrim.MOD_ID)
public class ModTileEntities {

  @SubscribeEvent
  public static void registerTileEntities(final RegistryEvent.Register<Block> event) {
    GameRegistry.registerTileEntity(EnchantedFlowerTileEntity.class, new ResourceLocation(Skrim.MOD_ID,
        "tile_entity_enchanted_flower"));
    GameRegistry.registerTileEntity(MegaChestTileEntity.class, new ResourceLocation(Skrim.MOD_ID,
        "tile_entity_mega_chest"));
    GameRegistry.registerTileEntity(CakeTileEntity.class, new ResourceLocation(Skrim.MOD_ID, "cake_tile_entity"));
  }

}
