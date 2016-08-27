package avi.mod.skrim.skills.smelting;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import avi.mod.skrim.Skrim;
import avi.mod.skrim.skills.SkillProvider;

public class SmeltingProvider {

    @CapabilityInject(ISkillSmelting.class)
    public static final Capability<ISkillSmelting> SMELTING = null;
    public static final EnumFacing DEFAULT_FACING = null;
    public static final ResourceLocation ID = new ResourceLocation(Skrim.modId, "SkillSmelting");

    public static void register() {
      CapabilityManager.INSTANCE.register(ISkillSmelting.class, SkillSmelting.skillStorage, SkillSmelting.class);
      MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    public static class EventHandler {

      @SubscribeEvent
      public void attachCapabilities(AttachCapabilitiesEvent.Entity event) {
        Entity player = event.getEntity();
        if (player instanceof EntityPlayer) {
          if (!player.hasCapability(SMELTING, EnumFacing.NORTH)) {
            event.addCapability(ID, createProvider());
          }
        }
      }

    }

    public static SkillProvider<ISkillSmelting> createProvider() {
    	return new SkillProvider<ISkillSmelting>(SMELTING, EnumFacing.NORTH);
    }

    public static SkillProvider<ISkillSmelting> createProvider(ISkillSmelting smelting) {
    	return new SkillProvider<ISkillSmelting>(SMELTING, EnumFacing.NORTH, smelting);
    }

}
