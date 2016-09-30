package avi.mod.skrim.proxy;

import java.io.File;

import avi.mod.skrim.Config;
import avi.mod.skrim.Skrim;
import avi.mod.skrim.blocks.ModBlocks;
import avi.mod.skrim.blocks.VariantEnum;
import avi.mod.skrim.blocks.flowers.FlowerBase.EnumFlowerType;
import avi.mod.skrim.capabilities.ModCapabilities;
import avi.mod.skrim.handlers.EventHandler;
import avi.mod.skrim.handlers.LoadSkillsHandler;
import avi.mod.skrim.items.ModItems;
import avi.mod.skrim.network.GuiHandler;
import avi.mod.skrim.network.SkrimPacketHandler;
import avi.mod.skrim.tileentity.ModTileEntities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CommonProxy {

	public static Configuration config;

	public void preInit(FMLPreInitializationEvent event) {
		File directory = event.getModConfigurationDirectory();
		config = new Configuration(new File(directory.getPath(), "modtut.cfg"));
		Config.readConfig();
		ModItems.createItems();
		ModBlocks.createBlocks();
		ModCapabilities.registerCapabilities();
		ModTileEntities.register();
		MinecraftForge.EVENT_BUS.register(new LoadSkillsHandler());
		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}

	public void init(FMLInitializationEvent event) {
		SkrimPacketHandler.registerPackets();
		NetworkRegistry.INSTANCE.registerGuiHandler(Skrim.instance, new GuiHandler());
	}

	public void postInit(FMLPostInitializationEvent event) {
		if (config.hasChanged()) {
			config.save();
		}
	}

	public EntityPlayer getPlayerEntity(MessageContext context) {
  	return context.getServerHandler().playerEntity;
  }

	public void registerItemRenderer(Item item, int meta, String id) {

	}

	public void registerMinecraftItemRenderer(Item item, int meta, String resource) {

	}

	public void registerBlockVariant(ItemBlock block, EnumFlowerType types[]) {

	}

}
