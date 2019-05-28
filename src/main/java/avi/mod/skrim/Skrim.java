package avi.mod.skrim;

import avi.mod.skrim.advancements.SkrimAdvancements;
import avi.mod.skrim.capabilities.SkrimCapabilities;
import avi.mod.skrim.client.SkrimTab;
import avi.mod.skrim.commands.CommandRegistry;
import avi.mod.skrim.handlers.EventHandler;
import avi.mod.skrim.handlers.GuiHandler;
import avi.mod.skrim.handlers.LoadSkillsHandler;
import avi.mod.skrim.network.SkrimPacketHandler;
import avi.mod.skrim.proxy.IProxy;
import avi.mod.skrim.skills.brewing.SkrimPotionRecipes;
import avi.mod.skrim.world.loot.CustomLootTables;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod(modid = Skrim.MOD_ID, name = Skrim.NAME, version = Skrim.VERSION, acceptedMinecraftVersions = "[1.12.2]")
public class Skrim {

  public static final String MOD_ID = "skrim";
  public static final String NAME = "Skrim";
  public static final String VERSION = "1.1.12-1.0";
  public static final SkrimTab CREATIVE_TAB = new SkrimTab();

  @SidedProxy(serverSide = "avi.mod.skrim.proxy.ServerProxy", clientSide = "avi.mod.skrim.proxy.ClientProxy")
  public static IProxy proxy;
  @Mod.Instance(MOD_ID)
  public static Skrim instance = new Skrim();

  @Mod.EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    proxy.preInit();
    SkrimPacketHandler.registerPackets();
  }

  @Mod.EventHandler
  public void init(FMLInitializationEvent event) {
    proxy.init();

    CustomLootTables.registerLootTables();
    SkrimCapabilities.registerCapabilities();
    SkrimAdvancements.register();
    SkrimPotionRecipes.registerRecipes();

    // Hook up all event handlers, this allows them to use Subscribe to Events
    MinecraftForge.EVENT_BUS.register(new LoadSkillsHandler());
    MinecraftForge.EVENT_BUS.register(new EventHandler());
    NetworkRegistry.INSTANCE.registerGuiHandler(Skrim.instance, new GuiHandler());
  }

  @Mod.EventHandler
  public void postInit(FMLPostInitializationEvent event) {
    proxy.postInit();
    // CookingPatch.apply();
  }

  @Mod.EventHandler
  public void serverStarting(FMLServerStartingEvent event) {
    CommandRegistry.registerCommands(event);
  }

}
