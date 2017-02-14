package avi.mod.skrim.tileentity;

import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModTileEntities {

	public static void register() {
		GameRegistry.registerTileEntity(EnchantedFlowerTileEntity.class, "tile_entity_enchanted_flower");
		GameRegistry.registerTileEntity(MegaChestTileEntity.class, "tile_entity_mega_chest");
	}

}
