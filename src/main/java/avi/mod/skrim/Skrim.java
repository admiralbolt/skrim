package avi.mod.skrim;

import avi.mod.skrim.client.SkrimTab;
import avi.mod.skrim.commands.CommandRegistry;
import avi.mod.skrim.patches.CookingPatch;
import avi.mod.skrim.proxy.IProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = Skrim.MOD_ID, name = Skrim.NAME, version = Skrim.VERSION, acceptedMinecraftVersions = "[1.12.1]")
public class Skrim {

  public static final String MOD_ID = "skrim";
  public static final String NAME = "Skrim";
  public static final String VERSION = "1.1.12-1.0";
  public static final SkrimTab CREATIVE_TAB = new SkrimTab();

  /**
   * The debug flag is used for logging several different
   * messages for debugging individual skills and abilities.
   * Should be FALSE for release.
   */
  public static final boolean DEBUG = true;

  /**
   * Whether or not to enforce only giving xp / bonuses
   * for NON player placed blocks.  If enforce=true
   * then NO experience will be given for breaking
   * player placed blocks.
   * Should be TRUE for release.
   */
  public static final boolean ENFORCE_NATURAL = true;

  /**
   * Pretty straight forward, every hit is a critical hit.
   * Should be FALSE for release.
   */
  public static final boolean ALWAYS_CRIT = false;

  @SidedProxy(serverSide = "avi.mod.skrim.proxy.ServerProxy", clientSide = "avi.mod.skrim.proxy.ClientProxy")
  public static IProxy proxy;
  @Mod.Instance(MOD_ID)
  public static Skrim instance = new Skrim();

  @Mod.EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    System.out.println(NAME + " is in preInit.");
    proxy.preInit();
  }

  @Mod.EventHandler
  public void init(FMLInitializationEvent event) {
    System.out.println(NAME + " is in init.");
    proxy.init();
  }

  @Mod.EventHandler
  public void postInit(FMLPostInitializationEvent event) {
    System.out.println(NAME + " is in postinit.");
    proxy.postInit();
    // CookingPatch.apply();
  }

  @Mod.EventHandler
  public void serverStarting(FMLServerStartingEvent event) {
    System.out.println("server starting...");
    CommandRegistry.registerCommands(event);
  }

}
