package avi.mod.skrim.commands;

import avi.mod.skrim.Skrim;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Skrim.MOD_ID)
public class CommandRegistry {

  @SubscribeEvent
  public static void registerCommands(FMLServerStartingEvent event) {
    event.registerServerCommand(new SetSkillCommand());
    event.registerServerCommand(new WTFDIDCommand());
    event.registerServerCommand(new CoordCommand());
  }

}
