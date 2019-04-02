package avi.mod.skrim.skills.farming;

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

public class FarmingProvider {

    @CapabilityInject(ISkillFarming.class)
    public static final Capability<ISkillFarming> FARMING = null;
    public static final EnumFacing DEFAULT_FACING = null;
    public static final ResourceLocation ID = new ResourceLocation(Skrim.MOD_ID, "SkillFarming");

    public static void register() {
      CapabilityManager.INSTANCE.register(ISkillFarming.class, SkillFarming.skillStorage, SkillFarming.class);
      MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    public static class EventHandler {

      @SubscribeEvent
      public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        Entity player = event.getObject();
        if (player instanceof EntityPlayer) {
          if (!player.hasCapability(FARMING, EnumFacing.NORTH)) {
            event.addCapability(ID, createProvider());
          }
        }
      }

    }

    public static SkillProvider<ISkillFarming> createProvider() {
    	return new SkillProvider<ISkillFarming>(FARMING, EnumFacing.NORTH);
    }

    public static SkillProvider<ISkillFarming> createProvider(ISkillFarming farming) {
    	return new SkillProvider<ISkillFarming>(FARMING, EnumFacing.NORTH, farming);
    }

}
