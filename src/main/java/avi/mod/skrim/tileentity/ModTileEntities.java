package avi.mod.skrim.tileentity;

import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModTileEntities {
	
	public static void register() {
    GameRegistry.registerTileEntity(TileEntityEnchantedFlower.class, "tile_entity_enchanted_flower");
	}

}
