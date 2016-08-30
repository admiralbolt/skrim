package avi.mod.skrim.proxy;

import java.io.File;
import avi.mod.skrim.Config;
import avi.mod.skrim.Skrim;
import avi.mod.skrim.blocks.ModBlocks;
import avi.mod.skrim.capabilities.ModCapabilities;
import avi.mod.skrim.handlers.ArtifactHandler;
import avi.mod.skrim.handlers.DeathEvent;
import avi.mod.skrim.handlers.GuiEventHandler;
import avi.mod.skrim.handlers.JoinWorldHandler;
import avi.mod.skrim.handlers.SkillHandler;
import avi.mod.skrim.items.ModItems;
import avi.mod.skrim.network.GuiHandler;
import avi.mod.skrim.network.SkrimPacketHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
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
		SkillHandler.register();
		ArtifactHandler.register();
		MinecraftForge.EVENT_BUS.register(new JoinWorldHandler());
		MinecraftForge.EVENT_BUS.register(new DeathEvent());
	}

	public void init(FMLInitializationEvent event) {
		SkrimPacketHandler.registerSkillPackets();
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

}
