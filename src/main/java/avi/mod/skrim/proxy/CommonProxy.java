package avi.mod.skrim.proxy;

import avi.mod.skrim.Config;
import avi.mod.skrim.Skrim;
import avi.mod.skrim.advancements.ModAdvancements;
import avi.mod.skrim.capabilities.ModCapabilities;
import avi.mod.skrim.entities.ModEntities;
import avi.mod.skrim.handlers.EventHandler;
import avi.mod.skrim.handlers.LoadSkillsHandler;
import avi.mod.skrim.init.SkrimSoundEvents;
import avi.mod.skrim.items.weapons.CustomBow;
import avi.mod.skrim.network.GuiHandler;
import avi.mod.skrim.network.SkrimPacketHandler;
import avi.mod.skrim.world.loot.CustomLootTables;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.File;

public class CommonProxy {

  public static Configuration config;

  public void preInit(FMLPreInitializationEvent event) {
    File directory = event.getModConfigurationDirectory();
    config = new Configuration(new File(directory.getPath(), "modtut.cfg"));
    Config.readConfig();
    CustomLootTables.registerLootTables();
    SkrimSoundEvents.register();
    ModCapabilities.registerCapabilities();
    ModEntities.register();
    MinecraftForge.EVENT_BUS.register(new LoadSkillsHandler());
    MinecraftForge.EVENT_BUS.register(new EventHandler());
  }

  public void init(FMLInitializationEvent event) {
    SkrimPacketHandler.registerPackets();
    for (ModAdvancements.CustomAdvancement advancement : ModAdvancements.ADVANCEMENTS_BY_NAME.values()) {
      System.out.println("registering trigger: " + advancement.trigger);
      CriteriaTriggers.register(advancement.trigger);
    }
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

  public void registerBowVariants(CustomBow customBow) {

  }

}
