package avi.mod.skrim.skills.fishing;

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

public class FishingProvider {

    @CapabilityInject(ISkillFishing.class)
    public static final Capability<ISkillFishing> FISHING = null;
    public static final EnumFacing DEFAULT_FACING = null;
    public static final ResourceLocation ID = new ResourceLocation(Skrim.MOD_ID, "SkillFishing");

    public static void register() {
      CapabilityManager.INSTANCE.register(ISkillFishing.class, SkillFishing.skillStorage, SkillFishing.class);
      MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    public static class EventHandler {

      @SubscribeEvent
      public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        Entity player = event.getObject();
        if (player instanceof EntityPlayer) {
          if (!player.hasCapability(FISHING, EnumFacing.NORTH)) {
            event.addCapability(ID, createProvider());
          }
        }
      }

    }

    public static SkillProvider<ISkillFishing> createProvider() {
    	return new SkillProvider<ISkillFishing>(FISHING, EnumFacing.NORTH);
    }

    public static SkillProvider<ISkillFishing> createProvider(ISkillFishing fishingg) {
    	return new SkillProvider<ISkillFishing>(FISHING, EnumFacing.NORTH, fishingg);
    }

}
