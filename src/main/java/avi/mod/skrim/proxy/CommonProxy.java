package avi.mod.skrim.proxy;

import java.io.File;

import avi.mod.skrim.Config;
import avi.mod.skrim.Skrim;
import avi.mod.skrim.blocks.ModBlocks;
import avi.mod.skrim.blocks.flowers.FlowerBase.EnumFlowerType;
import avi.mod.skrim.capabilities.ModCapabilities;
import avi.mod.skrim.entities.ModEntities;
import avi.mod.skrim.handlers.EventHandler;
import avi.mod.skrim.handlers.LoadSkillsHandler;
import avi.mod.skrim.init.SkrimSoundEvents;
import avi.mod.skrim.items.CustomBow;
import avi.mod.skrim.items.ModItems;
import avi.mod.skrim.network.GuiHandler;
import avi.mod.skrim.network.SkrimPacketHandler;
import avi.mod.skrim.stats.SkrimAchievements;
import avi.mod.skrim.tileentity.ModTileEntities;
import avi.mod.skrim.world.loot.CustomLootTables;
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
		CustomLootTables.registerLootTables();
		SkrimSoundEvents.register();
		ModItems.createItems();
		ModBlocks.createBlocks();
		ModCapabilities.registerCapabilities();
		ModTileEntities.register();
		ModEntities.register();
		MinecraftForge.EVENT_BUS.register(new LoadSkillsHandler());
		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}

	public void init(FMLInitializationEvent event) {
		SkrimPacketHandler.registerPackets();
		SkrimAchievements.register();
		NetworkRegistry.INSTANCE.registerGuiHandler(Skrim.instance, new GuiHandler());
	}

	public void postInit(FMLPostInitializationEvent event) {
		if (config.hasChanged()) {
			config.save();
		}
	}

	public EntityPlayer getPlayerEntity(MessageContext context) {
  	return context.getServerHandler().player;
  }

	public void registerItemRenderer(Item item, int meta, String id) {

	}

	public void registerMinecraftItemRenderer(Item item, int meta, String resource) {

	}

	public void registerBlockVariant(ItemBlock block, EnumFlowerType types[]) {

	}

	public void registerBowVariants(CustomBow customBow) {

	}

}
