package avi.mod.skrim.skills.digging;

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

public class DiggingProvider {

    @CapabilityInject(ISkillDigging.class)
    public static final Capability<ISkillDigging> DIGGING = null;
    public static final EnumFacing DEFAULT_FACING = null;
    public static final ResourceLocation ID = new ResourceLocation(Skrim.MOD_ID, "SkillDigging");

    public static void register() {
      CapabilityManager.INSTANCE.register(ISkillDigging.class, SkillDigging.skillStorage, SkillDigging::new);
      MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    public static class EventHandler {

      @SubscribeEvent
      public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        Entity player = event.getObject();
        if (player instanceof EntityPlayer) {
          if (!player.hasCapability(DIGGING, EnumFacing.NORTH)) {
            event.addCapability(ID, createProvider());
          }
        }
      }
      
    }

    public static SkillProvider<ISkillDigging> createProvider() {
    	return new SkillProvider<>(DIGGING, EnumFacing.NORTH);
    }

    public static SkillProvider<ISkillDigging> createProvider(ISkillDigging digging) {
    	return new SkillProvider<>(DIGGING, EnumFacing.NORTH, digging);
    }

}
