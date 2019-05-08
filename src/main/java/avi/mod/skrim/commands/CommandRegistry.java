package avi.mod.skrim.commands;

import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class CommandRegistry {

  public static void registerCommands(FMLServerStartingEvent event) {
    event.registerServerCommand(new SetSkillCommand());
    event.registerServerCommand(new WTFDIDCommand());
    event.registerServerCommand(new CoordCommand());
    event.registerServerCommand(new SkrimOptionCommand());
  }

}
