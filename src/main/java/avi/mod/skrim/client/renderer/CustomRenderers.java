package avi.mod.skrim.client.renderer;

import avi.mod.skrim.blocks.tnt.CustomTNTPrimed;
import avi.mod.skrim.entities.monster.BioCreeper;
import avi.mod.skrim.entities.monster.NapalmCreeper;
import avi.mod.skrim.tileentity.TileEntityEnchantedFlower;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CustomRenderers {

	@SideOnly(Side.CLIENT)
	public static void register() {
		RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnchantedFlower.class, new TileEntityEnchantedFlowerRenderer());
		RenderingRegistry.registerEntityRenderingHandler(CustomTNTPrimed.class, new EntityCustomTNTPrimedRenderer(Minecraft.getMinecraft().getRenderManager()));
		/**
		 * The fishing line is jank.  It has been axed accordingly.
		 * Sleep, sweet prince.
		 */
		// RenderingRegistry.registerEntityRenderingHandler(CustomFishHook.class, new RenderCustomFishHook(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(NapalmCreeper.class, new RenderCustomCreeper(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(BioCreeper.class, new RenderCustomCreeper(Minecraft.getMinecraft().getRenderManager()));
	}

}
