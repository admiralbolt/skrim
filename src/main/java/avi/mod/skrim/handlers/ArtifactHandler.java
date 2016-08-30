package avi.mod.skrim.handlers;

import net.minecraftforge.common.MinecraftForge;
import avi.mod.skrim.handlers.artifacts.*;

public class ArtifactHandler {

	public static void register() {
		MinecraftForge.EVENT_BUS.register(new SpringheelHandler());
		MinecraftForge.EVENT_BUS.register(new CanesHandler());
	}
	
}
