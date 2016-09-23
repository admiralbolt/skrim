package avi.mod.skrim.proxy;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.blocks.ModBlocks;
import avi.mod.skrim.blocks.VariantEnum;
import avi.mod.skrim.blocks.flowers.FlowerBase.EnumFlowerType;
import avi.mod.skrim.client.renderer.TileEntityEnchantedFlowerRenderer;
import avi.mod.skrim.handlers.GuiEventHandler;
import avi.mod.skrim.items.CustomFishHook;
import avi.mod.skrim.items.RenderCustomFishHook;
import avi.mod.skrim.tileentity.TileEntityEnchantedFlower;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClientProxy extends CommonProxy {

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		MinecraftForge.EVENT_BUS.register(new GuiEventHandler());
		// RenderingRegistry.registerEntityRenderingHandler(CustomFishHook.class, new RenderCustomFishHook(Minecraft.getMinecraft().getRenderManager()));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnchantedFlower.class, new TileEntityEnchantedFlowerRenderer());
	}

	@Override
	public void registerItemRenderer(Item item, int meta, String id) {
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(Skrim.modId + ":" + id, "inventory"));
	}

	@Override
	public void registerMinecraftItemRenderer(Item item, int meta, String resource) {
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(resource));
	}

	@Override
	public void registerBlockVariant(ItemBlock itemBlock, EnumFlowerType types[]) {
		Block block = itemBlock.getBlock();
		String baseName = block.getUnlocalizedName();
		Item item = itemBlock.getItemFromBlock(block);
		for (EnumFlowerType type : types) {
			ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation(Skrim.modId + ":" + baseName + "_" + type.getName(), "inventory");
			ModelLoader.setCustomModelResourceLocation(item, type.getMeta(), itemModelResourceLocation);
			ModelBakery.registerItemVariants(item, itemModelResourceLocation);
		}
	}

	@Override
	public EntityPlayer getPlayerEntity(MessageContext context) {
		return (context.side.isClient()) ? Minecraft.getMinecraft().thePlayer : super.getPlayerEntity(context);
	}

}
