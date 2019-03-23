package avi.mod.skrim;

import avi.mod.skrim.client.TestTab;
import avi.mod.skrim.commands.CommandRegistry;
import avi.mod.skrim.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = Skrim.modId, name = Skrim.name, version = Skrim.version, acceptedMinecraftVersions = "[1.12.1]")
public class Skrim {

	@SidedProxy(serverSide = "avi.mod.skrim.proxy.ServerProxy", clientSide = "avi.mod.skrim.proxy.ClientProxy")
	public static CommonProxy proxy;

	public static final String modId = "skrim";
	public static final String name = "Skrim";
	public static final String version = "1.1.9";

	public static final TestTab creativeTab = new TestTab();

	/**
	 * The debug flag is used for logging several different
	 * messages for debugging individual skills and abilities.
	 * Should be FALSE for release.
	 */
	public static final boolean DEBUG = false;

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

	@Mod.Instance(modId)
	public static Skrim instance = new Skrim();

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		System.out.println(this.name + " is in preInit.");
		this.proxy.preInit(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		System.out.println(this.name + " is in init.");
		this.proxy.init(event);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		System.out.println(this.name + " is in postinit.");
		this.proxy.postInit(event);
	}

	@Mod.EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		CommandRegistry.registerCommands(event);
	}

}
