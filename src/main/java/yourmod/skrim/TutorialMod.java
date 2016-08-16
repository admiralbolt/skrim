package yourmod.skrim;

import yourmod.skrim.item.ModItems;
import yourmod.skrim.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = TutorialMod.modId, name = TutorialMod.name, version = TutorialMod.version, acceptedMinecraftVersions = "[1.10.2]")
public class TutorialMod {

	@SidedProxy(serverSide = "yourmod.skrim.proxy.CommonProxy", clientSide = "yourmod.skrim.proxy.ClientProxy")
	public static CommonProxy proxy;

	public static final String modId = "tutorial";
	public static final String name = "Tutorial Mod";
	public static final String version = "1.0.0";

	@Mod.Instance(modId)
	public static TutorialMod instance;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		System.out.println(name + " is loading!");
		ModItems.init();
		System.out.println(name + " finished loading!");
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {

	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {

	}
}
