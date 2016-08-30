package avi.mod.skrim.proxy;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import avi.mod.skrim.Skrim;
import avi.mod.skrim.capabilities.ModCapabilities;
import avi.mod.skrim.handlers.DeathEvent;
import avi.mod.skrim.handlers.JoinWorldHandler;
import avi.mod.skrim.handlers.SkillHandler;
import avi.mod.skrim.network.SkrimPacketHandler;

public class ServerProxy extends CommonProxy {


	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
	}


}
