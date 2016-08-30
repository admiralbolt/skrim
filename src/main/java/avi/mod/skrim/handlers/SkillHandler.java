package avi.mod.skrim.handlers;

import net.minecraftforge.common.MinecraftForge;
import avi.mod.skrim.handlers.skills.*;

public class SkillHandler {

    public static void register() {
      MinecraftForge.EVENT_BUS.register(new BotanyHandler());
      MinecraftForge.EVENT_BUS.register(new CookingHandler());
      MinecraftForge.EVENT_BUS.register(new DefenseHandler());
      MinecraftForge.EVENT_BUS.register(new DiggingHandler());
      MinecraftForge.EVENT_BUS.register(new DemolitionHandler());
      MinecraftForge.EVENT_BUS.register(new FarmingHandler());
      MinecraftForge.EVENT_BUS.register(new FishingHandler());
      MinecraftForge.EVENT_BUS.register(new MeleeHandler());
      MinecraftForge.EVENT_BUS.register(new MiningHandler());
      MinecraftForge.EVENT_BUS.register(new RangedHandler());
      MinecraftForge.EVENT_BUS.register(new SmeltingHandler());
      MinecraftForge.EVENT_BUS.register(new WoodcuttingHandler());
    }

}
