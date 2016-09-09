package avi.mod.skrim.proxy;

import java.io.File;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import avi.mod.skrim.Config;
import avi.mod.skrim.Skrim;
import avi.mod.skrim.blocks.ModBlocks;
import avi.mod.skrim.capabilities.ModCapabilities;
import avi.mod.skrim.handlers.DeathEvent;
import avi.mod.skrim.handlers.EventHandler;
import avi.mod.skrim.handlers.JoinWorldHandler;
import avi.mod.skrim.handlers.skills.BotanyHandler;
import avi.mod.skrim.handlers.skills.CookingHandler;
import avi.mod.skrim.handlers.skills.DiggingHandler;
import avi.mod.skrim.handlers.skills.FarmingHandler;
import avi.mod.skrim.handlers.skills.FishingHandler;
import avi.mod.skrim.handlers.skills.WoodcuttingHandler;
import avi.mod.skrim.items.ModItems;
import avi.mod.skrim.network.GuiHandler;
import avi.mod.skrim.network.SkrimPacketHandler;

public class CommonProxy {

	public static Configuration config;

	public void preInit(FMLPreInitializationEvent event) {
		File directory = event.getModConfigurationDirectory();
		config = new Configuration(new File(directory.getPath(), "modtut.cfg"));
		Config.readConfig();
		ModItems.createItems();
		ModBlocks.createBlocks();
		ModCapabilities.registerCapabilities();
		registerSkills(); // Will be removed once all skills updated to new style of events.
		MinecraftForge.EVENT_BUS.register(new JoinWorldHandler());
		MinecraftForge.EVENT_BUS.register(new DeathEvent());
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
	
	public static void registerSkills() {
    MinecraftForge.EVENT_BUS.register(new BotanyHandler());
    MinecraftForge.EVENT_BUS.register(new CookingHandler());
    MinecraftForge.EVENT_BUS.register(new DiggingHandler());
    MinecraftForge.EVENT_BUS.register(new FarmingHandler());
    MinecraftForge.EVENT_BUS.register(new FishingHandler());
    MinecraftForge.EVENT_BUS.register(new WoodcuttingHandler());
  }

}
