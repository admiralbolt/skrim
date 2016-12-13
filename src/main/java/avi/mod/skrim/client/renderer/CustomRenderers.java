package avi.mod.skrim.client.renderer;

import avi.mod.skrim.blocks.tnt.CustomTNTPrimed;
import avi.mod.skrim.entities.monster.BioCreeper;
import avi.mod.skrim.entities.monster.MegaChicken;
import avi.mod.skrim.entities.monster.NapalmCreeper;
import avi.mod.skrim.entities.projectile.Rocket;
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
		RenderingRegistry.registerEntityRenderingHandler(NapalmCreeper.class, new RenderCustomCreeper(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(BioCreeper.class, new RenderCustomCreeper(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(Rocket.class, new RenderRocket(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(MegaChicken.class, new RenderMegaChicken(Minecraft.getMinecraft().getRenderManager()));
	}

}
