package avi.mod.skrim.proxy;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import avi.mod.skrim.Skrim;
import avi.mod.skrim.handlers.CustomKeyHandler;
import avi.mod.skrim.handlers.GuiEventHandler;

public class ClientProxy extends CommonProxy {

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		MinecraftForge.EVENT_BUS.register(new GuiEventHandler());
		// MinecraftForge.EVENT_BUS.register(new CustomKeyHandler());
	}

	@Override
	public void registerItemRenderer(Item item, int meta, String id) {
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(Skrim.modId + ":" + id, "inventory"));
	}

}
